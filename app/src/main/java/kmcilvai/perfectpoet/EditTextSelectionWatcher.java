package kmcilvai.perfectpoet;

import android.content.Context;
import android.util.AttributeSet;


public class EditTextSelectionWatcher extends android.support.v7.widget.AppCompatEditText {

    public EditTextSelectionWatcher(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);

    }

    public EditTextSelectionWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditTextSelectionWatcher(Context context) {
        super(context);

    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        int start = selStart;
        int end = selEnd;

        if (start != end) {
            MainActivity.inputSelection(start , end, this.getId());
        }



    }
}
