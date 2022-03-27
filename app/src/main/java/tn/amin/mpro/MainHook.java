package tn.amin.mpro;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.*;
import tn.amin.mpro.builders.MediaResourceBuilder;
import tn.amin.mpro.constants.Constants;
import tn.amin.mpro.constants.ReflectedClasses;
import tn.amin.mpro.features.commands.CommandData;
import tn.amin.mpro.features.commands.CommandsManager;
import tn.amin.mpro.features.image.ImageEditor;
import tn.amin.mpro.builders.MessengerDialogBuilder;
import tn.amin.mpro.internal.SendButtonOCL;
import tn.amin.mpro.utils.XposedHilfer;

import android.content.*;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.net.Uri;
import android.os.StrictMode;
import android.graphics.*;
import android.util.TypedValue;
import android.widget.*;
import android.view.*;
import android.graphics.drawable.*;
import android.app.*;

import androidx.core.content.res.ResourcesCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
	// We use WeakReference to prevent leaks
	public WeakReference<Activity> O_activity = null;
	public ArrayList<EditText> O_messageEdits = new ArrayList<>();
	public ArrayList<View> O_sendButtons = new ArrayList<>();
	public ArrayList<ViewGroup> O_composerViews = new ArrayList<>();
	public WeakReference<ViewGroup> O_contentView;

	public XModuleResources mResources = null;
	private final ConversationMapper mConversationMapper = new ConversationMapper();
	private PrefReader mPrefReader = null;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		Constants.MODULE_PATH = startupParam.modulePath;
		mResources = XModuleResources.createInstance(Constants.MODULE_PATH, null);
	}

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(Constants.TARGET_PACKAGE_NAME)) return;
		XposedHilfer.setClassLoader(lpparam.classLoader);
		MProMain.init(this);

		ConversationMapper.mainHook = this;
		CommandsManager.mainHook = this;
		CommandData.X_CommandInterface = MProMain.getReflectedClasses().X_CommandInterface;

		XposedBridge.log("MessengerPro hook successfully loaded");
		mPrefReader = new PrefReader();

		initHooks();
		if (Constants.MPRO_DEBUG) {
			initTestHooks();
			initDebugHooks();
		}
	}

	private void setupListeners() {
		// getActiveSendButton and getActiveMessageEdit will always return null because
		// the views were not shown yet
		View sendButton = O_sendButtons.get(O_sendButtons.size() - 1);
		EditText messageEdit = O_messageEdits.get(O_messageEdits.size() - 1);
		sendButton.setOnClickListener(new SendButtonOCL(sendButton, messageEdit));
		messageEdit.addTextChangedListener(mConversationMapper);
	}

	/**
	* Init the essential hooks for Messenger Pro to work
	* */
	private void initHooks() {
		ReflectedClasses classes =  MProMain.getReflectedClasses();

		/* When MainActivity is resumed: + capture its object (onResume gets called after onCreate)
	    *								 + reload preferences
	    *								 + prepare cache dir since messenger erases it after shutdown
		* */
		XposedBridge.hookAllMethods(Activity.class, "onResume", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (!param.thisObject.getClass().getName().equals("com.facebook.messenger.neue.MainActivity")) return;
				// Disable network restrictions
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);

				XposedBridge.log("MainActivity resumed...");

				O_activity = new WeakReference<>((Activity) param.thisObject);
				mPrefReader.reload();
				if (Constants.MPRO_CACHE_DIR == null) {
					Constants.MPRO_CACHE_DIR = new File(getContext().getCacheDir(), "fb_temp/mpro");
					if (!Constants.MPRO_CACHE_DIR.exists()) {
						Constants.MPRO_CACHE_DIR.mkdirs();
					}
				}
			}
		});

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
				O_messageEdits.add((EditText) XposedHelpers.getObjectField(container, "A0B"));

//				GradientDrawable border = new GradientDrawable();
//				border.setStroke(5, Color.BLACK);
//				container.setBackground(border);

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
				ArrayList result = (ArrayList) XposedHilfer.invokeOriginalMethod(param);
				if (result == null || result.size() == 0) return;

				Bitmap bitmapModel = ((BitmapDrawable)XposedHelpers.getObjectField(XposedHelpers.getObjectField(result.get(0), "A01"), "A04")).getBitmap();
				int dpi = bitmapModel.getDensity();
				int width = bitmapModel.getWidth();
				int height = bitmapModel.getHeight();

				BitmapDrawable drawable = (BitmapDrawable) ResourcesCompat.getDrawable(mResources, R.drawable.settings, null);
				Bitmap b = drawable.getBitmap();
				Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width / 2, height / 2, false);
				bitmapResized.setDensity(dpi);
				Drawable buttonIcon = new BitmapDrawable(mResources, bitmapResized);

				// It would have been easier to use XposedHelpers.findClass but using getParameterTypes()
				// will save time when updating obfuscated classes for a newer version
				Object buttonDataModel = result.get(0);
				Object buttonIconDataModel = XposedHelpers.getObjectField(buttonDataModel, "A01");
				Object buttonIconData = XposedHelpers.newInstance(buttonIconDataModel.getClass(), buttonIcon,
						Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
				Object buttonDataInit = buttonDataModel.getClass().getDeclaredConstructors()[0]
						.getParameterTypes()[0].newInstance();
				XposedHelpers.setObjectField(buttonDataInit, "A01", buttonIconData);
				XposedHelpers.setObjectField(buttonDataInit, "A04", "Messenger Pro Settings");
				XposedHelpers.setObjectField(buttonDataInit, "A05", Constants.TITLEBAR_BUTTON_ACTION_NAME);
				Object buttonData = XposedHelpers.newInstance(buttonDataModel.getClass(), buttonDataInit);
				result.add(buttonData);
				param.setResult(result);
			}
		});


		/*
		*  When user sends an image, we swap its uri with watermarked image
		* */
		XposedBridge.hookAllMethods(classes.X_MediaResourceHelper, "A0A", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!getPrefReader().isWatermarkEnabled()) return;

				Object mediaResource = param.args[0];
				Uri oldUri = ((Uri) XposedHelpers.getObjectField(mediaResource, "A0E"));
				Uri newUri = ImageEditor.onImageLoaded(oldUri.getPath());
				XposedHelpers.setObjectField(mediaResource, "A0E", newUri);
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
				if (requestCode == Constants.MPRO_ATTACHFILE_REQUEST_CODE) {
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

				Object iconType = Enum.valueOf(classes.X_IconType, "DOCUMENT_COMPLETE");
				Object colorType = Enum.valueOf(classes.X_ColorType, "RED");
				Object dataStoreInit = XposedHelpers.newInstance(classes.X_MoreDrawerGenericGridItemDataStoreInit);
				XposedHelpers.setObjectField(dataStoreInit, "A00", iconType);
				XposedHelpers.setObjectField(dataStoreInit, "A03", colorType);
				XposedHelpers.setObjectField(dataStoreInit, "A04", "Attach a file");
				XposedHelpers.setObjectField(dataStoreInit, "A05", Constants.MORE_DRAWER_ACTION_NAME);

				Object dataStore = XposedHelpers.newInstance(classes.X_MoreDrawerGenericGridItemDataStore, dataStoreInit);
				Object data = XposedHelpers.newInstance(classes.X_MoreDrawerGenericGridItemData, dataStore);

				AbstractCollection<Object> listOfViewHolders = (AbstractCollection<Object>)
						XposedHelpers.getObjectField(
						XposedHelpers.getObjectField(param.args[0], "A03"), "A02");
				ArrayList<Object> newListOfViewHolders = new ArrayList<Object>(listOfViewHolders);
				newListOfViewHolders.add(data);
				Object newList = XposedHelpers.newInstance(classes.X_RegularImmutableList, newListOfViewHolders.toArray(), newListOfViewHolders.size());
				XposedHelpers.setObjectField(XposedHelpers.getObjectField(param.args[0], "A03"),
						"A02", newList);
			}
		});

		/*
		 * TitleBarButton onClick, first argument is actionName
		 * */
		XposedHilfer.hookAllMethods("X.1nK", "BUb", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				String buttonName = (String) param.args[0];
				boolean cancelOriginalMethod = true;
				switch (buttonName) {
					case Constants.TITLEBAR_BUTTON_ACTION_NAME:
						Intent intent = new Intent("tn.amin.mpro.SETTINGS");
						getActivity().startActivity(intent);
						break;
					default:
						cancelOriginalMethod = false;
				}
				if (cancelOriginalMethod)
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
				if (actionName.equals(Constants.MORE_DRAWER_ACTION_NAME)) {
					MProMain.startFileChooser();
				}
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
							.setMessage("Are you sure you want to call ?") // TODO: add participant / group name
							.setPositiveButton("Yes", (textId, listener) -> {
								try {
									XposedHilfer.invokeOriginalMethod(param);
								} catch (Throwable e) {
									XposedBridge.log(e);
								}
							})
							.setNegativeButton("No", null)
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
				Object mediaResource = getPendingAttachment();
				// If an attachment is pending attach it to the message
				// and remove its text
				if (mediaResource != null) {
					Object[] mediaResourcesArray = {mediaResource};
					Object mediaResources = XposedHelpers.newInstance(classes.X_RegularImmutableList,
							mediaResourcesArray, 1);
					XposedHelpers.setObjectField(param.args[1], "A0h", mediaResources);
					// Remove the dummy text
					XposedHelpers.setObjectField(param.args[1], "A0Z", null);
					HashSet<String> newMessageTypes = new HashSet<String>();
					Set<String> originalMessageTypes = (Set<String>)
							XposedHelpers.getObjectField(param.args[1], "A1E");
					// Remove "text" from message types to prevent NullPointerException
					for (String messageType: originalMessageTypes) {
						if (messageType.equals("text")) continue;
						newMessageTypes.add(messageType);
					}
					XposedHelpers.setObjectField(param.args[1], "A1E", newMessageTypes);
				}
			}
		});

		/*
		* MentionsSearchAdapter.onBindViewHolder
		* If command type is -1 (its source is MPro), setup its icon and its onClickListener
		* */
		XposedBridge.hookAllMethods(classes.X_MentionsSearchAdapter, "BRu", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				int position = (Integer) param.args[1];
				View bindedView = (View) XposedHelpers.getObjectField(param.args[0], "A0I");
				Object mentionData = ((List) XposedHelpers.getObjectField(param.thisObject, "A03")).get(position);
				int type = (Integer) XposedHelpers.callMethod(mentionData, "B4K");
				if (type == -1) {
					bindedView.setOnClickListener(view -> {
						MProMain.showCommandsAutoComplete(null);
						String commandLiteral = "/" + XposedHelpers.callMethod(mentionData, "B3J");
						getActiveMessageEdit().setText(commandLiteral);
						getActiveMessageEdit().setSelection(commandLiteral.length());
					});
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

	}

	/**
	 * Includes various hooks useful for debugging and placing
	 * breakpoints
	 * */
	private void initDebugHooks() {
		ReflectedClasses classes = MProMain.getReflectedClasses();

		XposedBridge.hookAllMethods(View.class, "performClick", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				View view = (View) param.thisObject;
				View.OnClickListener defaultOCL = (View.OnClickListener) XposedHelpers
						.getObjectField(
								XposedHelpers.callMethod(view, "getListenerInfo"),
								"mOnClickListener"
						);
				if (defaultOCL == null)
					defaultOCL = view1 -> {
					};
				XposedBridge.log("performClick called: View.OnClickListener: " +
						defaultOCL.getClass().getName() + " View: " + view.getClass().getName());
			}
		});

		XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				StringBuilder path = new StringBuilder();
				boolean log = false;
				if (param.args.length == 2) {
					if (param.args[0] instanceof String)
						path.append((String) param.args[0]);
					else
						log = false;
					path.append("/");
					path.append((String) param.args[1]);
				} else if (param.args[0] instanceof String)
					path.append((String) param.args[0]);
				else
					path.append(((URI) param.args[0]).getPath());
				if (log)
					XposedBridge.log("Accessing File: " + path.toString());
//				if (path.toString().startsWith("/storage/emulated/0/DCIM/Camera/test.mp4")) {
//					XposedBridge.log(new Throwable());
//				}
			}
		});
		XposedHelpers.findAndHookMethod(classes.X_ResourcesImpl, "loadXmlResourceParser",
				String.class, "int", "int", String.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						String file = (String) param.args[0];
						int id = (Integer) param.args[1];
						int assetCookie = (Integer) param.args[2];
						String type = (String) param.args[3];
						XposedBridge.log("Loading " + type + " from file " + file);

//						if (file.equals("r/el3.xml")) {
//							XposedBridge.log(new Throwable());
//						}
					}
				});

		XposedBridge.hookAllConstructors(classes.X_MediaResource, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Object mediaResource = param.thisObject;
				Uri uri = (Uri) XposedHelpers.getObjectField(mediaResource, "A0E");
//				XposedBridge.log("A new MediaResource was created with path: " +
//						uri.getPath());
			}
		});

		XposedBridge.hookAllMethods(classes.X_ComponentHost, "setContentDescription", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			}
		});

		XposedBridge.hookAllMethods(classes.X_ResourcesImpl, "loadDrawableForCookie", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				Resources wrapper = (Resources) param.args[0];
				TypedValue value = (TypedValue) param.args[1];
				Integer id = (Integer) param.args[2];
				Integer density = (Integer) param.args[3];

				final String file = value.string.toString();
			}
		});

		XposedBridge.hookAllConstructors(classes.X_Message, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
				param.setResult(null);
			}
		});

		// OneLineComposerView onActionSent
		XposedBridge.hookAllMethods(classes.X_OneLineComposerView, "A08", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedBridge.hookAllConstructors(classes.X_MediaResource, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.79Z", "A00", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.1jh", "A0F", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		// Any ComponentHost onClickListener is A18 in class param.thisObject
		XposedHilfer.hookAllMethods("X.1Jl", "APb", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.3Aj", "onClick", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.3Ag", "BSJ", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.B7D", "run", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.77b", "A16", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedBridge.hookAllMethods(Dialog.class, "show", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.GPo", "A00", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.2tN", "A00", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});

		XposedBridge.hookAllMethods(AssetManager.class, "open", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!((String) param.args[0]).equals("strings/default.frsc"))
					super.beforeHookedMethod(param);
			}
		});

		XposedHilfer.hookAllMethods("X.0wT", "getString", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				super.afterHookedMethod(param);
			}
		});

		XposedHilfer.hookAllConstructors("com.facebook.messaging.attachments.OtherAttachmentData", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				super.beforeHookedMethod(param);
			}
		});
	}

	public PrefReader getPrefReader() { return mPrefReader; }
	public ConversationMapper getConversationMapper() { return mConversationMapper; }
	public ViewGroup getActiveComposerView() {
		ViewGroup composerView = (ViewGroup) getActiveView(O_composerViews);
		return composerView == null ? O_composerViews.get(0) : composerView;
	}
	public View getActiveSendButton() { return O_sendButtons.get(
			O_composerViews.indexOf(getActiveComposerView())
	); }
	public EditText getActiveMessageEdit() { return O_messageEdits.get(
			O_composerViews.indexOf(getActiveComposerView())
	); }
	public Object getActiveCommandsParser() {
		ViewGroup composerView = getActiveComposerView();
		if (composerView == null) return null;
		return XposedHelpers.getObjectField(composerView, "A0c");
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
	public static ArrayList<Object> mDebugObjects = new ArrayList<>();
}
