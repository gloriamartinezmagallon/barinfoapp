package barinfo.navdev.barinfo.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import barinfo.navdev.barinfo.R;

/**
 * An extension of the seek bar which adds the progress text to the left of the seek bar thumb. (if progress bar
 * is determinate else a constant value of 0 is shown so use this view only when using determinate progress bar).
 */
public class SeekBarWithHint extends android.support.v7.widget.AppCompatSeekBar {
    private Paint seekBarHintPaint;
    private int hintTextColor;
    private float hintTextSize;

    public SeekBarWithHint(Context context) {
        super(context);
        init();
    }

    public SeekBarWithHint(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SeekBarWithHint,
                0, 0);

        try {
            hintTextColor = a.getColor(R.styleable.SeekBarWithHint_hint_text_color, 0);
            hintTextSize = a.getDimension(R.styleable.SeekBarWithHint_hint_text_size, 0);
        } finally {
            a.recycle();
        }

        init();
    }

    public SeekBarWithHint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        seekBarHintPaint = new TextPaint();
        seekBarHintPaint.setColor(hintTextColor);
        seekBarHintPaint.setTextAlign(Paint.Align.CENTER);
        seekBarHintPaint.setTextSize(hintTextSize);
    }

    public void setHintTextColor(int hintTextColor) {
        this.hintTextColor = hintTextColor;
    }

    public void setHintTextSize(float hintTextSize) {
        this.hintTextSize = hintTextSize;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int thumb_x = (int) (((double) this.getProgress() / this.getMax()) * (double) this.getWidth()) + 10;
        int middle = getHeight() - 4;
        String progress = getProgress()+"m";
        if (getProgress() > 1000){
            progress = String.format( "%.1fkm", getProgress()/1000f);
        }else if (getProgress() < 100){
            progress ="";
        }
        canvas.drawText(progress, thumb_x, hintTextSize, seekBarHintPaint);
    }
}