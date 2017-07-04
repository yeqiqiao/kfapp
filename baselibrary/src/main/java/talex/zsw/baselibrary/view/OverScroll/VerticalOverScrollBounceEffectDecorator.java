package talex.zsw.baselibrary.view.OverScroll;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import talex.zsw.baselibrary.view.OverScroll.adapters.IOverScrollDecoratorAdapter;

/**
 * A concrete implementation of {@link OverScrollBounceEffectDecoratorBase} for a vertical orientation.
 *
 * @author amit
 */
public class VerticalOverScrollBounceEffectDecorator extends OverScrollBounceEffectDecoratorBase
{

	protected static class MotionAttributesVertical extends MotionAttributes
	{

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public boolean init(View view, MotionEvent event)
		{

			// We must have history available to calc the dx. Normally it's there - if it isn't temporarily,
			// we declare the event 'invalid' and expect it in consequent events.
			if (event.getHistorySize() == 0)
			{
				return false;
			}

			mAbsOffset = view.getTranslationY();
			mDeltaOffset = (event.getY(0) - event.getHistoricalY(0, 0));
			mDir = mDeltaOffset > 0;

			return true;
		}
	}

	protected static class AnimationAttributesVertical extends AnimationAttributes
	{

		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		public AnimationAttributesVertical()
		{
			mProperty = View.TRANSLATION_Y;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void init(View view)
		{
			mAbsOffset = view.getTranslationY();
			mMaxOffset = view.getHeight();
		}
	}

	/**
	 * C'tor, creating the effect with default arguments:
	 * <br/>Touch-drag ratio in 'forward' direction will be set to DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD.
	 * <br/>Touch-drag ratio in 'backwards' direction will be set to DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK.
	 * <br/>Deceleration factor (for the bounce-back effect) will be set to DEFAULT_DECELERATE_FACTOR.
	 *
	 * @param viewAdapter The view's encapsulation.
	 */
	public VerticalOverScrollBounceEffectDecorator(IOverScrollDecoratorAdapter viewAdapter)
	{
		this(viewAdapter, DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD, DEFAULT_TOUCH_DRAG_MOVE_RATIO_BCK,
			DEFAULT_DECELERATE_FACTOR);
	}

	/**
	 * C'tor, creating the effect with explicit arguments.
	 *
	 * @param viewAdapter       The view's encapsulation.
	 * @param touchDragRatioFwd Ratio of touch distance to actual drag distance when in 'forward' direction.
	 * @param touchDragRatioBck Ratio of touch distance to actual drag distance when in 'backward'
	 *                          direction (opposite to initial one).
	 * @param decelerateFactor  Deceleration factor used when decelerating the motion to create the
	 *                          bounce-back effect.
	 */
	public VerticalOverScrollBounceEffectDecorator(IOverScrollDecoratorAdapter viewAdapter,
		float touchDragRatioFwd, float touchDragRatioBck, float decelerateFactor)
	{
		super(viewAdapter, decelerateFactor, touchDragRatioFwd, touchDragRatioBck);

		// Some setup on the view itself.
		mViewAdapter.getView().setOnTouchListener(this);
		mViewAdapter.getView().setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	@Override
	protected MotionAttributes createMotionAttributes()
	{
		return new MotionAttributesVertical();
	}

	@Override
	protected AnimationAttributes createAnimationAttributes()
	{
		return new AnimationAttributesVertical();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
	protected void translateView(View view, float offset)
	{
		view.setTranslationY(offset);
	}

	@Override
	protected void translateViewAndEvent(View view, float offset, MotionEvent event)
	{
		view.setTranslationY(offset);
		event.offsetLocation(offset - event.getY(0), 0f);
	}
}