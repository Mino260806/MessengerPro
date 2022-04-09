package tn.amin.mpro.features;
import android.text.*;

import tn.amin.mpro.MProMain;
import tn.amin.mpro.features.commands.CommandsManager;
import tn.amin.mpro.features.formatting.MessageFormatter;

public class ConversationMapper implements TextWatcher
{
	private static final CommandsManager mCommandsManager = new CommandsManager();

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
	}

	@Override
	public void afterTextChanged(Editable p1) {
		if (MProMain.getActiveCommandsParser() == null) return;
		mCommandsManager.update(p1.toString());
	}
	
	public boolean beforeMessageSent(Editable message) {if (MProMain.getPrefReader().isDontSendCommandEnabled()) {
		if (message.toString().startsWith("/")
				&& mCommandsManager.isLastCommandValid()) {
					return false;
			}
		}
		if (MProMain.getPrefReader().isTextFormattingEnabled()) {
			if (MProMain.formatNextMessage) {
				MessageFormatter.format(message);
			}
			else {
				MProMain.formatNextMessage = true;
			}
		}
		return true;
	}
	
	public void afterMessageSent(String sentMessage, boolean cancelled) {
		if (cancelled) {
			// Clear message input even if message was not sent
			MProMain.clearMessageInput();
		}
		if (MProMain.getPrefReader().isCommandsEnabled()) {
			if (sentMessage.startsWith("/")) {
				mCommandsManager.execute();
			}
		}
	}
}
