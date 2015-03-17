package at.markushi.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.markushi.circlebutton.R;

public class CircleButtonTextWithImage extends RelativeLayout {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private static final int PRESSED_RING_ALPHA = 75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

    private int centerY;
    private int centerX;
    private int outerRadius;
    private int pressedRingRadius;

    private Paint circlePaint;
    private Paint focusPaint;

    private float animationProgress;

    private int pressedRingWidth;
    private int defaultColor = Color.BLACK;
    private int pressedColor;
    private ObjectAnimator pressedAnimator;

    private ImageView imageView;
    private TextView textView;

    public CircleButtonTextWithImage(Context context) {
        super(context);
        init(context, null);
    }

    public CircleButtonTextWithImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleButtonTextWithImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (circlePaint != null) {
            circlePaint.setColor(pressed ? pressedColor : defaultColor);
        }

        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
        canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = Math.min(w, h) / 2;
        pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
    }

    public void setSize(int side) {
        setSize(side, side);
    }

    public void setSize(int w, int h) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        setLayoutParams(layoutParams);

        imageView.setMaxWidth(w / 3);
        imageView.setMaxHeight(h / 3);
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }

    public void setColor(int color) {
        this.defaultColor = color;
        this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

        circlePaint.setColor(defaultColor);
        focusPaint.setColor(defaultColor);
        focusPaint.setAlpha(PRESSED_RING_ALPHA);

        this.invalidate();
    }

    private void hidePressedRing() {
        pressedAnimator.setFloatValues(pressedRingWidth, 0f);
        pressedAnimator.start();
    }

    private void showPressedRing() {
        pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
        pressedAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        this.setFocusable(true);
        this.setGravity(Gravity.CENTER);
        setClickable(true);

        imageView = new ImageView(context);
        imageView.setId(R.id.cb_icon);
        textView = new TextView(context);

        RelativeLayout.LayoutParams imageViewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        imageViewLayoutParams.addRule(CENTER_HORIZONTAL);

        RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewLayoutParams.addRule(CENTER_HORIZONTAL);
        textViewLayoutParams.addRule(BELOW, imageView.getId());

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);

        focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPaint.setStyle(Paint.Style.STROKE);

        pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());

        int color = Color.BLACK;
        int padding = (int) (4 * getResources().getDisplayMetrics().density + 0.5f);
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
            color = a.getColor(R.styleable.CircleButton_cb_color, color);
            padding = (int) a.getDimension(R.styleable.CircleButton_cb_image_spacing, padding);
            pressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, pressedRingWidth);
            a.recycle();
        }

        setColor(color);
        textView.setPadding(0, padding, 0, 0);

        addView(imageView, imageViewLayoutParams);
        addView(textView, textViewLayoutParams);

        focusPaint.setStrokeWidth(pressedRingWidth);
        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
        pressedAnimator.setDuration(pressedAnimationTime);
    }

    private int getHighlightColor(int color, int amount) {
        return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageResource(int resId) {
        imageView.setImageResource(resId);
    }

    public void setImageDrawable(Drawable icon) {
        imageView.setImageDrawable(icon);
    }
}
