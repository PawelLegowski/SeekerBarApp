package pl.legowski;

import pl.legowski.seekerbarapp.R;
import android.widget.SeekBar;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;



/**
 * MySeekBar is an extension of SeekBar that implements
 * functionalities described by Your e-mail
 *
 * @author Pawe~l ~L~egowski
 * @since 2015-06-16
 */
public class MySeekBar extends SeekBar {
	private int iTimer;
	private boolean bIsDraggingView, bIsPerformingLongClick;
	private Handler handler;    
	private float fTouchX, fTouchY;
	private float fTouchStartX, fTouchStartY;
	private float fTextSize;
	private Paint textPaint;
	private boolean bAlignLeft;
	private int iProgressPosition;
	
	/**
	 * Runnable for scheduling time incrementation
	 */
	Runnable runnableIncrementTime = new Runnable() {
		
		@Override
		public void run() {
			iTimer++;			
			handler.postDelayed(runnableIncrementTime, 1000);
			invalidate();
		}
	};
	
	/**
	 * Runnable for distinguishing long click and activating drag mode
	 */
	Runnable runnableEnableDragView = new Runnable() {
		
		@Override
		public void run() {
			bIsDraggingView = true;
		}
	};
	
    public MySeekBar(Context context) {
        super(context);
        
        //Setting additional attributes
        init(null);
        
    }
    
    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Getting additional attributes from Layout
        TypedArray atribs = context.getTheme().obtainStyledAttributes(
        	attrs, R.styleable.MySeekBar, 0, 0);
        
        //Setting additional attributes
	    init(atribs);
        
    }    
    
    
    public MySeekBar(Context context, AttributeSet attrs, int defStyle)
    {
    	super(context,attrs,defStyle);
    	
        //Getting additional attributes from Layout
        TypedArray atribs = context.getTheme().obtainStyledAttributes(
        	attrs, R.styleable.MySeekBar, defStyle, 0);

        //Setting additional attributes
        init(atribs);        
    }
    
    /**
     * Setter function for text (counter) color
     * 
     * @param color			desired color
     */
    public void setTextColor(int color)
    {
    	textPaint.setColor(color);
    	invalidate();
    }
    
    /**
     * Setter function for text (counter) size
     * 
     * @param textSize		desired text size
     */
    public void setTextSize(float textSize)
    {
    	textPaint.setTextSize(textSize);
    	if(bAlignLeft)
        {	        	
        	setPadding((int) (getPaddingLeft() - fTextSize + textSize), 
        			getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
        else
        {
        	setPadding(getPaddingLeft(), getPaddingTop(), (int)	
        			(getPaddingRight() - fTextSize + textSize), getPaddingBottom());
        }
    	invalidate();
    }
    
    /**
     * Setter function for text (counter) align
     * 
     * @param bToLeft		true for Left, false for Right
     */
    public void setTextAlign(boolean bToLeft)
    {
    	if(bToLeft != bAlignLeft)
    	{
    		bAlignLeft = bToLeft;
	    	if(bAlignLeft)
	        {	        	
	    		textPaint.setTextAlign(Align.LEFT);
	        	setPadding((int) (getPaddingLeft() + fTextSize), getPaddingTop(), 
	        			(int) (getPaddingRight() - fTextSize), getPaddingBottom());
	        }
	        else
	        {
	        	textPaint.setTextAlign(Align.RIGHT);
	        	setPadding((int) (getPaddingLeft() - fTextSize), getPaddingTop(), 
	        			(int) (getPaddingRight() + fTextSize), getPaddingBottom());
	        }
    	}
    	invalidate();
    }
    
    /**
     * Function initializing additional Layout Params
     * 
     * @param params		additional params
     */
    private void init(TypedArray params) {
    	handler			= new Handler();
    	handler.postDelayed(runnableIncrementTime, 1000);
    	
        textPaint 	= new Paint();
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);			
        
		if(params==null)
		{			
			fTextSize = 40.0f;
	        textPaint.setColor(Color.BLACK);
	        bAlignLeft 		= true;	     
		}
		else
		{
	        try {
	        	textPaint.setColor(params.getColor(
	        			R.styleable.MySeekBar_textColor, Color.BLACK));
	        	bAlignLeft=params.getBoolean(
	        			R.styleable.MySeekBar_textAlignLeft, true);
	        	fTextSize=params.getDimension(
	        			R.styleable.MySeekBar_textSize, 40.0f);
	        } finally {
	        	params.recycle();
	        }	        	      
		}
		textPaint.setTextSize(fTextSize);
		if(bAlignLeft)
		{
			textPaint.setTextAlign(Align.LEFT);
			setPadding((int) (getPaddingLeft() + fTextSize), getPaddingTop(),
        			getPaddingRight(), getPaddingBottom());
		}
		else
		{
			textPaint.setTextAlign(Align.RIGHT);
			setPadding(getPaddingLeft(), getPaddingTop(), (int) (getPaddingRight()
        			+ fTextSize), getPaddingBottom());
		}
		iProgressPosition = getProgress();
	}       

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isEnabled())
    	{
    		switch (event.getAction())
    		{
    		case MotionEvent.ACTION_DOWN:				
    			fTouchX = event.getRawX();
    			fTouchY = event.getRawY();
    			fTouchStartX = fTouchX;
    			fTouchStartY = fTouchY;
    			bIsPerformingLongClick = true;
    			handler.postDelayed(runnableEnableDragView, 500);
    			return true;
    		case MotionEvent.ACTION_MOVE:
    			if(bIsDraggingView)
    			{
    				setX(getX() + event.getRawX() - fTouchX);
    				setY(getY() + event.getRawY() - fTouchY);
    				fTouchX = event.getRawX();
    				fTouchY = event.getRawY();
    				return true;
    			}
    			else if(bIsPerformingLongClick)
    			{
    				fTouchX = event.getRawX();
    				fTouchY = event.getRawY();
    				if(Math.sqrt(Math.pow(fTouchStartX - fTouchX,2) 
							+ Math.pow(fTouchStartY - fTouchY, 2)) > 50)
    				{
    					bIsPerformingLongClick = false;
    					handler.removeCallbacks(runnableEnableDragView);
    				}
    				return true;
    			}				
    			break;					
    		case MotionEvent.ACTION_UP:
    			int iCurrentProgress, iTargetProgress;				
    			bIsPerformingLongClick = false;
    			handler.removeCallbacks(runnableEnableDragView);
    			if(bIsDraggingView)
    			{
    				setX(getX()+event.getRawX() - fTouchX);
    				setY(getY()+event.getRawY() - fTouchY);
    				bIsDraggingView = false;	
    				return true;
    			}				
    			boolean ans = super.onTouchEvent(event);
    			iCurrentProgress=getProgress();
    			if(iCurrentProgress > 0.75*getMax())
    				iTargetProgress = getMax();
    			else if(iCurrentProgress < 0.25*getMax())				
    				iTargetProgress = 0;
    			else
    				iTargetProgress = (int)(0.5 * getMax());
    			if(iProgressPosition != iTargetProgress)
    			{
    				iProgressPosition = iTargetProgress;					
    				iTimer = 0;
    				handler.removeCallbacks(runnableIncrementTime);
    				handler.postDelayed(runnableIncrementTime, 1000);
    			}
    			if(iTargetProgress != iCurrentProgress)
    			{										
    				ObjectAnimator animator = ObjectAnimator.ofInt(this,
							"progress", iCurrentProgress, iTargetProgress);
    				animator.setInterpolator(new DecelerateInterpolator());
    				animator.setDuration(500);
    				animator.start();					
    			}
    			return ans;
    		}
    	}
		return super.onTouchEvent(event);
	}

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    	canvas.drawText(Integer.toString(iTimer), bAlignLeft ? 0 : getWidth(),
    			getHeight() * 0.9f, textPaint);
    }
}
