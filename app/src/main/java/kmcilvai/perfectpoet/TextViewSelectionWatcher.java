package kmcilvai.perfectpoet;

import android.content.Context;
import android.util.AttributeSet;


public class TextViewSelectionWatcher extends android.support.v7.widget.AppCompatTextView{

    public TextViewSelectionWatcher(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);

    }

    public TextViewSelectionWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public TextViewSelectionWatcher(Context context) {
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
