package tn.amin.mpro.internal.ui;

import java.util.AbstractCollection;
import java.util.ArrayList;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.constants.ReflectedClasses;

public class MoreDrawerItemsExtender {
    private final ArrayList<MoreDrawerItemModel> mItemModels = new ArrayList();

    public void extend(Object drawerItemsQuery) {
        ReflectedClasses classes = MProMain.getReflectedClasses();

        AbstractCollection<Object> listOfViewHolders = (AbstractCollection<Object>)
                XposedHelpers.getObjectField(
                XposedHelpers.getObjectField(
                drawerItemsQuery, "A03"), "A02");
        ArrayList<Object> newListOfViewHolders = new ArrayList(listOfViewHolders);

        for (MoreDrawerItemModel itemModel: mItemModels) {
            Object iconType = Enum.valueOf(classes.X_IconType, itemModel.getIconType());
            Object colorType = Enum.valueOf(classes.X_ColorType, itemModel.getIconColor());
            Object dataStoreInit = XposedHelpers.newInstance(classes.X_MoreDrawerGenericGridItemDataStoreInit);
            XposedHelpers.setObjectField(dataStoreInit, "A00", iconType);
            XposedHelpers.setObjectField(dataStoreInit, "A03", colorType);
            XposedHelpers.setObjectField(dataStoreInit, "A04", itemModel.getText());
            XposedHelpers.setObjectField(dataStoreInit, "A05", itemModel.getAction());

            Object dataStore = XposedHelpers.newInstance(classes.X_MoreDrawerGenericGridItemDataStore, dataStoreInit);
            Object data = XposedHelpers.newInstance(classes.X_MoreDrawerGenericGridItemData, dataStore);
            newListOfViewHolders.add(data);
        }
        Object newList = XposedHelpers.newInstance(classes.X_RegularImmutableList, newListOfViewHolders.toArray(), newListOfViewHolders.size());
        XposedHelpers.setObjectField(XposedHelpers.getObjectField(drawerItemsQuery, "A03"),
                "A02", newList);
    }

    public MoreDrawerItemsExtender addItem(MoreDrawerItemModel itemModel) {
        mItemModels.add(itemModel);
        return this;
    }

    public static class MoreDrawerItemModel {
        private final String mAction;
        private final String mText;
        private final String mIconColor;
        private final String mIconType;

        public MoreDrawerItemModel(String text, String iconType, String iconColor, String action) {
            mText = text;
            mIconType = iconType;
            mIconColor = iconColor;
            mAction = action;
        }

        public String getIconType() { return mIconType; }
        public String getIconColor() { return mIconColor; }
        public String getText() { return mText; }
        public String getAction() { return mAction; }
    }
}
