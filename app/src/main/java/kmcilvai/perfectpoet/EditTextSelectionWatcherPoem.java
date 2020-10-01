package kmcilvai.perfectpoet;

import android.content.Context;
import android.util.AttributeSet;


public class EditTextSelectionWatcherPoem extends android.support.v7.widget.AppCompatEditText {

    public EditTextSelectionWatcherPoem(Context context, AttributeSet attrs,
                                        int defStyle) {
        super(context, attrs, defStyle);

    }

    public EditTextSelectionWatcherPoem(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditTextSelectionWatcherPoem(Context context) {
        super(context);

    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        int start = selStart;
        int end = selEnd;
        if(this.getText().toString().length()>11) {
            if (selStart > this.length() - 11) {
                start = this.length() - 11;
            }
            if (selEnd > this.length() - 11) {
                end = this.length() - 11;
            }
            this.setSelection(start, end);
        }
        if (start != end) {
            MainActivity.inputSelection(start , end, this.getId());
        }



    }
}
