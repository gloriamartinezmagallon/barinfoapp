package barinfo.navdev.barinfo.Utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;


public class AutoTextSizeTextView extends android.support.v7.widget.AppCompatTextView {
    public AutoTextSizeTextView(Context context) {
        super(context);
    }

    public AutoTextSizeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoTextSizeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        int lineCount = getLineCount();
        while(getLineCount()>2){
            setTextSize(getTextSize()-1);
        }
    }



    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        int lineCount = getLineCount();
        while(getLineCount()>2){
            setTextSize(getTextSize()-1);
        }

        getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                int lineCount = getLineCount();
                while(getLineCount()>2){
                    setTextSize(getTextSize()-1);
                }
            }
        });
    }
}
