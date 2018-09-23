package agency.tango.materialintroscreen.behaviours;

import android.widget.Button;

import agency.tango.materialintroscreen.fragments.SlideFragmentBase;

/** Handler for the message button */
@SuppressWarnings("unused")
public class MessageButtonBehaviour {

    /** The handler for clicks on the button */
    private MessageButtonClickListener clickListener;

    /** The message of the button */
    private String messageButtonText;

    /**
     * Creates a new button behaviour
     * @param messageButtonText The message for the button
     * @param clickListener The click listener
     */
    public MessageButtonBehaviour(String messageButtonText, MessageButtonClickListener clickListener) {
        this.clickListener = clickListener;
        this.messageButtonText = messageButtonText;
    }

    /**
     * Getter for the click listener
     * @return The interface to be called for each click on the button
     */
    public MessageButtonClickListener getClickListener() {
        return clickListener;
    }

    /**
     * Getter for the button message text
     * @param slide The slide
     * @return The text to be shown on the button, or null if no button shall be shown
     */
    public String getMessageButtonText(SlideFragmentBase slide) {
        return messageButtonText;
    }

    /** Handler for on click */
    public interface MessageButtonClickListener {
        void onClick(Button messageButton);
    }
}
