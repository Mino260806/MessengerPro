package tn.amin.mpro;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.*;
import tn.amin.mpro.builders.dialog.LoadingDialogBuilder;
import tn.amin.mpro.builders.MediaResourceBuilder;
import tn.amin.mpro.constants.Constants;
import tn.amin.mpro.constants.ReflectedClasses;
import tn.amin.mpro.features.ConversationMapper;
import tn.amin.mpro.features.biometric.BiometricConversationLock;
import tn.amin.mpro.features.commands.CommandData;
import tn.amin.mpro.features.image.ImageEditor;
import tn.amin.mpro.builders.dialog.MessengerDialogBuilder;
import tn.amin.mpro.internal.Compatibility;
import tn.amin.mpro.internal.Debugger;
import tn.amin.mpro.internal.ListenerGetter;
import tn.amin.mpro.internal.ui.MoreDrawerItemsExtender;
import tn.amin.mpro.internal.ui.MoreDrawerItemsExtender.MoreDrawerItemModel;
import tn.amin.mpro.internal.ui.SendButtonOCL;
import tn.amin.mpro.internal.ui.MessageUtil;
import tn.amin.mpro.internal.ui.TitleBarButtonsExtender;
import tn.amin.mpro.internal.ui.TitleBarButtonsExtender.TitleBarButtonModel;
import tn.amin.mpro.storage.PrefReader;
import tn.amin.mpro.storage.StorageManager;
import tn.amin.mpro.utils.XposedHilfer;

import android.content.*;

import android.content.res.XModuleResources;
import android.net.Uri;
import android.os.StrictMode;
import android.widget.*;
import android.view.*;
import android.graphics.drawable.*;
import android.app.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.documentfile.provider.DocumentFile;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
	// We use WeakReference to prevent leaks
	public WeakReference<Activity> O_activity = null;
	public ArrayList<EditText> O_messageEdits = new ArrayList();
	public ArrayList<View> O_sendButtons = new ArrayList();
	public ArrayList<View> O_likeButtons = new ArrayList();
	public ArrayList<ViewGroup> O_composerViews = new ArrayList();
	public ArrayList<Object> O_composeFragments = new ArrayList();
	public WeakReference<ViewGroup> O_contentView;
	public WeakReference<Object> O_threadViewMessagesFragment = null;

	private boolean mIsInitialized = false;

	public XModuleResources mResources = null;
	private ConversationMapper mConversationMapper;
	private PrefReader mPrefReader = null;
	private BiometricConversationLock mBiometricConversationLock = null;
	private TitleBarButtonsExtender mTitleBarButtonsExtender;
	private MoreDrawerItemsExtender mMoreDrawerItemsExtender;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		Constants.MODULE_PATH = startupParam.modulePath;
		mResources = XModuleResources.createInstance(Constants.MODULE_PATH, null);
	}

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(Constants.TARGET_PACKAGE_NAME)) return;
		XposedHilfer.setClassLoader(lpparam.classLoader);

		if (Constants.MPRO_DEBUG) {
		    MProMain.getReflectedClasses().init();
			Debugger.initDebugHooks();
        }

		/*
		* Save data when activity is paused
		* */
		XposedBridge.hookAllMethods(Activity.class, "onPause", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!param.thisObject.getClass().getName().equals("com.facebook.messenger.neue.MainActivity")) return;

				if (mBiometricConversationLock != null)
					StorageManager.put(StorageManager.PAPER_LOCKED_THREAD_KEYS, mBiometricConversationLock.getLockedThreadKeys());

				new Thread(StorageManager::save).start();
			}
		});

		/* When MainActivity is resumed: + check for messenger version, if supported, init everything
		 *								 + capture activity object (onResume gets called after onCreate)
		 *								 + reload preferences
		 *								 + prepare cache dir since messenger erases it after shutdown
		 * */
		XposedBridge.hookAllMethods(Activity.class, "onResume", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
			    if (!param.thisObject.getClass().getName().equals("com.facebook.messenger.neue.MainActivity")) return;
			    XposedBridge.log("MainActivity resumed...");
				O_activity = new WeakReference<>((Activity) param.thisObject);

				// Check if messenger version is correct before doing anything.
				if (!mIsInitialized) {
					if (!Compatibility.isSupported(getContext())) {
						new AlertDialog.Builder(getContext())
								.setTitle("Unsupported")
								.setMessage("This messenger version is not supported. " +
										"Please visit our github page for more info. " +
										"You will continue to use messenger but Messenger Pro will not be activated.")
								.setCancelable(false)
								.setPositiveButton("Ok", (dialogInterface, i) -> {
								})
								.show();
						return;
					}
					else {
						// We set ClassLoader again, in case facebook altered it
						XposedHilfer.setClassLoader(param.thisObject.getClass().getClassLoader());
						init();
					}
				}

				// Disable network restrictions
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);

				if (Constants.MPRO_CACHE_DIR == null) {
					Constants.MPRO_CACHE_DIR = new File(getContext().getCacheDir(), "fb_temp/mpro");
					if (!Constants.MPRO_CACHE_DIR.exists()) {
						Constants.MPRO_CACHE_DIR.mkdirs();
					}
				}
			}
		});

		// This must be called before anything is initialized
		try {
			ReflectedClasses classes = new ReflectedClasses();
			classes.initUrgent();
			XposedHilfer.hookAllConstructors("X.0us", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					MProMain.classInjector = param.args[0];
					XposedBridge.unhookMethod(param.method, this);
				}
			});
		} catch (Throwable ignored) {
		}
	}

	private void setupListeners() {
		// getActiveSendButton and getActiveMessageEdit will always return null because
		// the views were not shown yet
		View sendButton = O_sendButtons.get(O_sendButtons.size() - 1);
		EditText messageEdit = O_messageEdits.get(O_messageEdits.size() - 1);
		sendButton.setOnClickListener(new SendButtonOCL(sendButton, messageEdit));
		messageEdit.addTextChangedListener(mConversationMapper);

		View likeButton = O_likeButtons.get(O_likeButtons.size() - 1);
		MProMain.setupLikeButtonListener(O_likeButtons.get(O_likeButtons.size() - 1));
	}

	private void init() {
		XposedBridge.log("MProMain.init()");
        MProMain.init(this);

        XposedBridge.log("StorageManager.init()");
		StorageManager.init();
		if (Constants.MPRO_DEBUG) {
			ArrayList<String> lockedThreadKeys = StorageManager.read(StorageManager.PAPER_LOCKED_THREAD_KEYS);
			mBiometricConversationLock = new BiometricConversationLock(lockedThreadKeys);
		}

		mConversationMapper = new ConversationMapper();
        XposedBridge.log("new ConversationMapper()");
        mPrefReader = new PrefReader();
        XposedBridge.log("new PrefReader()");
        mTitleBarButtonsExtender = new TitleBarButtonsExtender()
				.addButton(new TitleBarButtonModel(R.drawable.titlebar_power_center, "Messenger Pro power center", Constants.TITLEBAR_CONTROL_CENTER_ACTION))
				.addButton(new TitleBarButtonModel(R.drawable.titlebar_settings, "Messenger Pro settings", Constants.TITLEBAR_SETTINGS_ACTION));
        XposedBridge.log("new ToolBarButtonsExtender()");
		mMoreDrawerItemsExtender = new MoreDrawerItemsExtender()
				.addItem(new MoreDrawerItemModel("Attach file", "DOCUMENT_COMPLETE", "RED", Constants.MORE_DRAWER_ATTACHFILE_ACTION));
//				.addItem(new MoreDrawerItemModel("Schedule message", "CLOCK", "BLUE_GREY_75", Constants.MORE_DRAWER_SCHEDULEMESSAGE_ACTION));
		XposedBridge.log("new ToolBarButtonsExtender()");

        initHooks();
        XposedBridge.log("initHooks()");
        if (Constants.MPRO_DEBUG) {
            initTestHooks();
        }
        XposedBridge.log("MessengerPro hook successfully loaded");
        mIsInitialized = true;
	}

	/**
	* Init the essential hooks for Messenger Pro to work
	* */
	private void initHooks() {
		ReflectedClasses classes =  MProMain.getReflectedClasses();

		/*
		* This function gets called once / twice in messenger's lifecycle:
		* 	+ When it's opened
		*   + When a chat bubble is opened
		* */
		XposedBridge.hookAllMethods(classes.X_ComposeFragment, "onCreateView", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				final ViewGroup oneLineComposerView = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "A0B");
				final FrameLayout container = (FrameLayout) XposedHelpers.getObjectField(oneLineComposerView, "A0P");

				O_composerViews.add(oneLineComposerView);
				O_sendButtons.add((ImageView) XposedHelpers.getObjectField(oneLineComposerView, "A0O"));
				O_likeButtons.add((ImageView) XposedHelpers.getObjectField(getActiveComposerViewOrDefault(), "A0N"));
				O_messageEdits.add((EditText) XposedHelpers.getObjectField(container, "A0B"));

				O_composeFragments.add(
						XposedHelpers.getObjectField(
						XposedHelpers.getObjectField(
						oneLineComposerView, "A0L"), "A00"));
//				O_threadViewMessagesFragment = new WeakReference(
//						XposedHelpers.getObjectField(
//						XposedHelpers.getObjectField(
//						O_composeFragments.get(O_composeFragments.size()-1), "A0G"), "A00"));

				setupListeners();
				XposedBridge.log("Found all views successfully");

//				MProMain.showTutorial();
			}
		});

		/*
		* We capture contentView whenever it is set
		* */
		XposedBridge.hookAllMethods(Activity.class, "setContentView", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (!(param.args[0] instanceof Activity)) return;
				O_contentView = new WeakReference<>((ViewGroup) ((Activity) param.args[0]).getWindow().getDecorView());
			}
		});

		/*
		* This function returns an arrayList with info about TitleBarButtons.
		*  We add Messenger Pro Settings Button from here
		* */
		XposedHilfer.hookAllMethods("X.1nK", "B1t", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				ArrayList buttonModels = (ArrayList) XposedHilfer.invokeOriginalMethod(param);
				if (buttonModels == null || buttonModels.size() == 0) return;

				mTitleBarButtonsExtender.extend(buttonModels);
				param.setResult(buttonModels);
			}
		});


		/*
		*  When user sends an image, we swap its uri with watermarked image
		* */
		XposedBridge.hookAllMethods(classes.X_MediaResourceHelper, "A0A", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				if (!getPrefReader().isWatermarkEnabled()) return;
//
//				Object mediaResource = param.args[0];
//				Uri oldUri = ((Uri) XposedHelpers.getObjectField(mediaResource, "A0E"));
//				Uri newUri = ImageEditor.onImageLoaded(oldUri.getPath());
//				XposedHelpers.setObjectField(mediaResource, "A0E", newUri);
			}
		});

		/*
		*  This is onActivityResult, its called when an activity started using startActivityForResult
		*  returns. In our case, we need this after summoning file chooser to attach a file
		* */
		XposedHilfer.hookAllMethods("com.facebook.base.activity.FbFragmentActivity", "onActivityResult", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				int requestCode = (Integer) param.args[0];
				int resultCode = (Integer) param.args[1];
				Intent data = (Intent) param.args[2];

				XposedBridge.log("onActivityResult: " + requestCode);

				switch (requestCode) {
					case Constants.MPRO_ATTACHFILE_REQUEST_CODE: {
						if (resultCode == Activity.RESULT_OK) {
							Uri selectedFile = data.getData();
							if (selectedFile != null) {
								DocumentFile documentFile = DocumentFile.fromSingleUri(getContext(), selectedFile);
								String fileName = documentFile.getName();
								InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedFile);
								MProMain.sendAttachment(MediaResourceBuilder.createFromFile(fileName, inputStream).build());
							}
						}
						// Cancel FbFragmentActivity.onActivityResult to prevent crash
						// because of unknown requestCode
						param.setResult(null);
						break;
					}
					case Constants.MPRO_SETTINGS_REQUEST_CODE: {
						if (resultCode == Activity.RESULT_OK) {
							mPrefReader.reload();
						}
						param.setResult(null);
						break;
					}
					case Constants.MPRO_RETRIEVE_SHARED_PREFS_REQUEST_CODE: {
						if (resultCode == Activity.RESULT_OK) {
							String prefPath = data.getStringExtra("prefPath");
							HashMap<String, ?> prefMap = (HashMap<String, ?>)
									data.getSerializableExtra("prefMap");
							if (!mPrefReader.setSharedPreferences(prefPath)) {
								mPrefReader.setSharedPreferences(prefMap);
							}
						}
						break;
					}
				}
			}
		});
		/*
		* Shows "Attach a file" item in more actions menu (near camera button)
		* */
		XposedHilfer.hookAllMethods("X.BXR", "BRu", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
				param.setResult(null);

				mMoreDrawerItemsExtender.extend(param.args[0]);
			}
		});

		/*
		 * TitleBarButton onClick
		 * */
		XposedHilfer.hookAllMethods("X.1nK", "BUb", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				String actionName = (String) param.args[0];
				switch (actionName) {
					case Constants.TITLEBAR_SETTINGS_ACTION:
						MProMain.startSettings(Constants.MPRO_SETTINGS_TYPE_SETTINGS);
						break;
					case Constants.TITLEBAR_CONTROL_CENTER_ACTION:
						MProMain.startSettings(Constants.MPRO_SETTINGS_TYPE_POWER_CENTER);
						break;
					default:
						return;
				}
				param.setResult(null);
			}
		});

		/*
		 * MoreDrawerItem onClick
		 * */
		XposedHilfer.hookAllMethods("X.2mI", "A01", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
				String actionName = (String) XposedHelpers.getObjectField(param.args[0], "A05");
				switch (actionName) {
					case Constants.MORE_DRAWER_ATTACHFILE_ACTION:
						MProMain.startFileChooser();
						break;
					case Constants.MORE_DRAWER_SCHEDULEMESSAGE_ACTION:
						// TODO
						break;
					default:
						return;
				}
				param.setResult(null);
			}
		});

		/*
		* These methods get called when a user presses a call button in title bar
		* */
		for (String methodName: new String[] { "A0A", "A0B", "A0C" }) {
			XposedHilfer.hookAllMethods("X.9vp", methodName, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!MProMain.getPrefReader().isCallConfirmationEnabled()) return;
					new MessengerDialogBuilder()
							.setTitle("Confirm")
							.setMessage(mResources.getString(R.string.dialog_call_confirm)) // TODO: add participant / group name
							.setPositiveButton(mResources.getString(android.R.string.yes), (textId, listener) -> {
								try {
									XposedHilfer.invokeOriginalMethod(param);
								} catch (Throwable e) {
									XposedBridge.log(e);
								}
							})
							.setNegativeButton(mResources.getString(android.R.string.no), null)
							.build()
							.show();
					param.setResult(null);
				}
			});
		}

		/*
		* Gets called when user sends any message
		* */
		XposedBridge.hookAllMethods(classes.X_ComposeFragment, "A1d", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				Object pendingAttachment = getPendingAttachment();
				// If an attachment is pending attach it to the message and remove its text
				if (pendingAttachment != null) {
					Object[] mediaResourcesArray = { pendingAttachment };
					Object mediaResources = XposedHelpers.newInstance(classes.X_RegularImmutableList,
							mediaResourcesArray, 1);
					XposedHelpers.setObjectField(param.args[1], "A0h", mediaResources);
					// Remove the dummy text
					MessageUtil.removeAttribute(param.args[1], "text");
					XposedHelpers.setObjectField(param.args[1], "A0Z", null);
				} else if (isLikePending()) {
					int likeSize = getPendingLikeSize();
					String likeStickerId = "";
					switch (likeSize) {
						case 0: {
							likeStickerId = "369239263222822";
							break;
						}
						case 1: {
							likeStickerId = "369239343222814";
							break;
						}
						case 2: {
							likeStickerId = "369239383222810";
							break;
						}
						default: {
							return;
						}
					}

					Class<Enum> X_MessageSource = (Class<Enum>) param.args[0].getClass();
					Enum messageSource = Enum.valueOf(X_MessageSource, "COMPOSER_HOT_LIKE");
					param.args[0] = messageSource;

					MessageUtil.removeAttribute(param.args[1], "text");
					XposedHelpers.setObjectField(param.args[1], "A0Z", null);
					MessageUtil.setStickerId(param.args[1], likeStickerId);
				}
				// If no attachment is pending, check if user sent an image in case we want to apply
				// watermarks.
				else if (MProMain.getPrefReader().isWatermarkEnabled()) {
					AbstractCollection mediaResources = (AbstractCollection) XposedHelpers.getObjectField(param.args[1], "A0h");
					if (mediaResources.size() > 0) {
						final LoadingDialogBuilder loadingDialog = new LoadingDialogBuilder()
								.setText(mResources.getString(R.string.dialog_watermark));
						MProMain.getActivity().runOnUiThread(loadingDialog::show);

						// Watermarking may take a long time, so do it in a separate thread to prevent
						// messenger from freezing
						new Thread(() -> {
							for (Object mediaResource: mediaResources) {
								Enum mediaType = (Enum) XposedHelpers.getObjectField(mediaResource, "A0P");
								String mediaTypeString = mediaType.name();
								boolean isImage = mediaTypeString.equals("PHOTO");
								if (isImage) {
									Uri oldUri = ((Uri) XposedHelpers.getObjectField(mediaResource, "A0E"));
									Uri newUri = ImageEditor.onImageLoaded(oldUri.getPath());
									XposedHelpers.setObjectField(mediaResource, "A0E", newUri);
								}
							}
							MProMain.getActivity().runOnUiThread(loadingDialog::dismiss);

							try {
								XposedHilfer.invokeOriginalMethod(param);
							} catch (Throwable throwable) {
								XposedBridge.log(throwable);
							}
						}).start();

						// Prevent original method from getting executed since we are going to do it after
						// watermarking is finished
						param.setResult(null);
					}
				}
			}
		});

		/*
		* MentionsSearchAdapter.onBindViewHolder
		* If its source is MPro, setup its icon and its onClickListener
		* */
		XposedBridge.hookAllMethods(classes.X_MentionsSearchAdapter, "BRu", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				int position = (Integer) param.args[1];
				View bindedView = (View) XposedHelpers.getObjectField(param.args[0], "A0I");
				Object mentionData = ((List) XposedHelpers.getObjectField(param.thisObject, "A03")).get(position);
				int type = (Integer) XposedHelpers.callMethod(mentionData, "B4K");
				if (type == Constants.MPRO_MENTIONS_AUTOCOMPLETE_TYPE) {
					bindedView.setOnClickListener(new CommandData.OnClickListener(mentionData));
					// Assign view icon
					String[] split = ((String) XposedHelpers.callMethod(mentionData, "AtU")).split("/");
					String drawableResourceName = split[split.length - 1];
					int drawableResource = mResources.getIdentifier(drawableResourceName, "drawable", Constants.MPRO_PACKAGE_NAME);
					if (drawableResource > 0) {
						Drawable drawable = ResourcesCompat.getDrawable(mResources, drawableResource, null);
						ImageView fbDraweeView = (ImageView) XposedHelpers.getObjectField(bindedView, "A00");
						fbDraweeView.setImageDrawable(drawable);
					}
				}
				else if (ListenerGetter.from(bindedView).getOnClickListener() instanceof CommandData.OnClickListener) {
					bindedView.setOnClickListener(XposedHilfer.getFirstObjectFieldByType(param.thisObject, View.OnClickListener.class));
				}
			}
		});

		/*
		* This returns a list of Mentions after typing @
		* If its called by MPro it gets executes again with an empty list
		* preventing commands auto complete from showing.
		* */
		XposedHilfer.hookAllMethods("X.2pe", "A04", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				List list = (List) param.args[1];
				int size;
				if (list == null) {
					size = -1;
				} else {
					size = list.size();
				}

				if (size == 0)
					param.setResult(null);
			}
		});
	}

	/**
	 * Includes unstable hooks not yet released
	 * */
	private void initTestHooks() {
		ReflectedClasses classes = MProMain.getReflectedClasses();

		XposedHilfer.hookAllMethods("X.4v9", "A00", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Object dataHolder =
						XposedHelpers.getObjectField(
						XposedHelpers.getObjectField(param.thisObject,
						"A02"), "A00");
				Object threadKey =
						XposedHelpers.getObjectField(
						XposedHelpers.getObjectField(
						XposedHelpers.getObjectField(
						XposedHelpers.getObjectField(param.thisObject,
						"A02"), "A00"),
						"A02"), // ThreadSummary
						"A0g"); // ThreadKey
				AbstractCollection longClickItems = (AbstractCollection)
						XposedHelpers.getObjectField(dataHolder, "A05");

				ArrayList newLongClickItems =  new ArrayList(longClickItems);
				Class<?> X_LongClickItem = newLongClickItems.get(0).getClass();

				Object newLongClickItem;
				if (!mBiometricConversationLock.isConversationLocked(threadKey.toString())) {
					newLongClickItem = XposedHelpers.newInstance(X_LongClickItem,
							Enum.valueOf(classes.X_IconType, "LOCK"),
							"Lock",
							"This will only allow authenticated users to access this conversation",
							"lock",
							Constants.MPRO_LOCK_CONVERSATION_ACTION_ID,
							0);
				}
				else {
					newLongClickItem = XposedHelpers.newInstance(X_LongClickItem,
							Enum.valueOf(classes.X_IconType, "UNLOCK"),
							"Unlock",
							"This will disable authentication for this conversation",
							"unlock",
							Constants.MPRO_UNLOCK_CONVERSATION_ACTION_ID,
							0);
				}
				newLongClickItems.add(newLongClickItem);

				XposedHelpers.setObjectField(dataHolder, "A05",
						XposedHelpers.callStaticMethod(classes.X_ImmutableList, "copyOf", newLongClickItems));
			}
		});

		XposedHilfer.hookAllMethods("X.Adx", "Blg", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				int itemId = XposedHelpers.getIntField(param.args[0], "A00");
				boolean isIdApproved = true;
				boolean unlock = false;
				Object threadKey = null;
				switch (itemId) {
					case Constants.MPRO_UNLOCK_CONVERSATION_ACTION_ID:
						unlock = true;
					case Constants.MPRO_LOCK_CONVERSATION_ACTION_ID:
						threadKey =
								XposedHelpers.getObjectField(
								XposedHelpers.getObjectField(
								XposedHelpers.getObjectField(
								XposedHelpers.getObjectField(
								XposedHelpers.getObjectField(param.thisObject,
										"A00"), "A00"), "A00"),
										"A1L"), // ThreadSummary
										"A0g"); // ThreadKey
						break;
					default:
						isIdApproved = false;
						break;
				}
				if (isIdApproved) {
					param.setResult(null);
				}
				if (threadKey != null) {
					// Dismiss long click dialog
					XposedHelpers.callMethod(
					XposedHelpers.getObjectField(
					XposedHelpers.getObjectField(
					param.thisObject, "A01"), "A02"), "A00");

					if (unlock) {
						mBiometricConversationLock.unlockConversation(threadKey.toString());
					} else {
						mBiometricConversationLock.lockConversation(threadKey.toString());
					}
				}
			}
		});

		// This method opens any conversation (does not work in chatheads and bubbles)
		XposedHilfer.hookAllMethods("X.1Ri", "A0C", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				Object threadKey = XposedHelpers.getObjectField(param.args[0], "A02");
				if (mBiometricConversationLock.isConversationLocked(threadKey.toString())) {
					mBiometricConversationLock.accessConversation(param);
					param.setResult(null);
				}
			}
		});

		// When a message is received
		XposedHelpers.findAndHookMethod(ContentValues.class, "put", String.class, String.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (param.args[0].equals("montage_reply_message_id")) {
					String msgTimestamp = ((ContentValues) param.thisObject).getAsString("timestamp_sent_ms");
					if (msgTimestamp == null || msgTimestamp.isEmpty()) {
						msgTimestamp = ((ContentValues) param.thisObject).getAsString("timestamp_ms");
					}
					// If message was sent before messenger was started, no need to react,
					// or MPro wil spam reacts on many messages.
					if (Long.parseLong(msgTimestamp) < MProMain.startupTimeMillis) return;
					if (!getPrefReader().isUserBusy()) return;
					String msgText = ((ContentValues) param.thisObject).getAsString("text");
					String msgId = ((ContentValues) param.thisObject).getAsString("msg_id");
					String sender = ((ContentValues) param.thisObject).getAsString("sender");
					String threadKeyString = ((ContentValues) param.thisObject).getAsString("thread_key");
					if (threadKeyString == null || sender == null) {
						XposedBridge.log(new Throwable("thread_key cannot be null"));
					}
					if (!threadKeyString.split(":")[0].equals("ONE_TO_ONE"))
						return;
					if (msgId.startsWith("mid.")) {
						Object threadKey = XposedHelpers.callStaticMethod(MProMain.getReflectedClasses().X_ThreadKey, "A0F", threadKeyString);
						Object lastThreadKey = getLastThreadKey();
						// If I'm not in the conversation
						if (threadKey != null && threadKey.equals(lastThreadKey)) return;

						JSONObject senderJson = new JSONObject(sender);
						String senderFbId = senderJson.getString("user_key").split(":")[1];
						// If I did not send the message
						if (senderFbId.equals(MProMain.user.getFbId())) return;

						MProMain.getActivity().runOnUiThread(() -> {
							MProMain.reactToMessage(threadKey, msgId, mPrefReader.getBusyEmoji());
						});
					}
				}
			}
		});
	}

	public PrefReader getPrefReader() { return mPrefReader; }
	public ConversationMapper getConversationMapper() { return mConversationMapper; }
	public ViewGroup getActiveComposerViewOrNull() {
		ViewGroup composerView = getActiveView(O_composerViews);
		return composerView;
	}
	public ViewGroup getActiveComposerViewOrDefault() {
		ViewGroup composerView = getActiveView(O_composerViews);
		return composerView == null ? O_composerViews.get(0) : composerView;
	}
	public View getActiveSendButton() { return O_sendButtons.get(
			O_composerViews.indexOf(getActiveComposerViewOrDefault())
	); }
	public EditText getActiveMessageEdit() { return O_messageEdits.get(
			O_composerViews.indexOf(getActiveComposerViewOrDefault())
	); }
	public Object getActiveCommandsParser() {
		ViewGroup composerView = getActiveComposerViewOrDefault();
		return XposedHelpers.getObjectField(composerView, "A0c");
	}
	public Object getActiveComposeFragmentOrNull() {
		Object composerView = getActiveComposerViewOrNull();
		return composerView != null ? O_composeFragments.get(O_composerViews.indexOf(composerView)) : null;
	}
	public Object getLastThreadKey() {
		Object composerFragment = getActiveComposeFragmentOrNull();
		if (composerFragment == null) return null;
		return XposedHelpers.callStaticMethod(
			O_composeFragments.get(0).getClass(), "A00", composerFragment);
	}

	public Activity getActivity() { return O_activity.get(); }
	public Context getContext() { return O_activity.get(); }
	private <T> T getActiveView(ArrayList<T> viewArray) {
		for (T v: viewArray) {
			if (((View) v).isShown())
				return v;
		}
		return null;
	}

	private Object mPendingAttachment = null;
	public void setPendingAttachment(Object mediaResource) { mPendingAttachment = mediaResource; }
	public Object getPendingAttachment() {
		Object attachment = mPendingAttachment;
		mPendingAttachment = null;
		return attachment;
	}

	/**
	 * I use this function to hook a method on the air using evaluate expression
	 * in android studio debugger after breakpoint is reached
	 * */
	private static void hookMethodAtRuntime(String className, String method) {
		XposedHilfer.hookAllMethods(className, method, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param); // Place breakpoint here
				XposedHilfer.invokeOriginalMethod(param);
				super.afterHookedMethod(param); // Place breakpoint here
			}
		});
	}

	private int pendingLikeSize = -1;
	public boolean isLikePending() {
		return pendingLikeSize != -1;
	}
	public int getPendingLikeSize() {
		int field = pendingLikeSize;
		pendingLikeSize = -1;
		return field;
	}
	public void setPendingLikeSize(int pendingLikeSize) { this.pendingLikeSize = pendingLikeSize; }
}
