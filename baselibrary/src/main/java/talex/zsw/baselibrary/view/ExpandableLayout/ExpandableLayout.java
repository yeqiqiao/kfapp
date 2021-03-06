/***********************************************************************************
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Robin Chutaux
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/
package talex.zsw.baselibrary.view.ExpandableLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import talex.zsw.baselibrary.R;

public class ExpandableLayout extends RelativeLayout
{
	private Boolean isAnimationRunning = false;
	private Boolean isOpened = false;
	private Integer duration;
	private FrameLayout contentLayout;
	private FrameLayout headerLayout;
	private FrameLayout footerLayout;
	private ImageView mImageView;
	private int openID, closeID;
	private ScrollView scrollView;

	public ExpandableLayout(Context context)
	{
		super(context);
	}

	public ExpandableLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	public ExpandableLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(final Context context, AttributeSet attrs)
	{
		final View rootView = View.inflate(context, R.layout.view_expandablelayout, this);
		headerLayout = (FrameLayout) rootView.findViewById(R.id.view_expandable_headerlayout);
		footerLayout = (FrameLayout) rootView.findViewById(R.id.view_expandable_footerLayout);
		final TypedArray typedArray =
			context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
		final int headerID =
			typedArray.getResourceId(R.styleable.ExpandableLayout_EL_headerLayout, -1);
		final int contentID =
			typedArray.getResourceId(R.styleable.ExpandableLayout_EL_contentLayout, -1);
		final int footerID =
			typedArray.getResourceId(R.styleable.ExpandableLayout_EL_footerLayout, -1);
		contentLayout = (FrameLayout) rootView.findViewById(R.id.view_expandable_contentLayout);

		if (headerID == -1 || contentID == -1)
		{
			throw new IllegalArgumentException("HeaderLayout and ContentLayout cannot be null!");
		}

		if (isInEditMode())
		{
			return;
		}

		duration = typedArray.getInt(R.styleable.ExpandableLayout_EL_duration,
			getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));

		if (headerID>0)
		{
			final View headerView = View.inflate(context, headerID, null);
			headerView.setLayoutParams(
				new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			headerLayout.addView(headerView);
		}
		else
		{
			headerLayout.setVisibility(GONE);
		}

		if (footerID>0)
		{
			final View footerView = View.inflate(context, footerID, null);
			footerView.setLayoutParams(
				new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			footerLayout.addView(footerView);
		}
		else
		{
			footerLayout.setVisibility(GONE);
		}

		final View contentView = View.inflate(context, contentID, null);
		contentView.setLayoutParams(
			new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		contentLayout.addView(contentView);

		contentLayout.setVisibility(GONE);
		headerLayout.setOnClickListener(listener);
		footerLayout.setOnClickListener(listener);
		typedArray.recycle();
	}

	private OnClickListener listener =new OnClickListener()
	{
		@Override public void onClick(View v)
		{
			if (!isAnimationRunning)
			{
				if (contentLayout.getVisibility() == VISIBLE)
				{
					collapse(contentLayout);
				}
				else
				{
					expand(contentLayout);
				}

				isAnimationRunning = true;
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						isAnimationRunning = false;
					}
				}, duration);
			}
			if (scrollView != null)
			{
				scrollView.smoothScrollBy(1, 1);
			}
		}
	};

	private void expand(final View v)
	{
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();
		v.getLayoutParams().height = 0;
		v.setVisibility(VISIBLE);
		if (mImageView != null)
		{
			mImageView.setImageResource(openID);
		}

		Animation animation = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				if (interpolatedTime == 1)
				{
					isOpened = true;
				}
				v.getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT :
					(int) (targetHeight * interpolatedTime);
				v.requestLayout();
			}


			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};
		animation.setDuration(duration);
		v.startAnimation(animation);
	}

	private void collapse(final View v)
	{
		final int initialHeight = v.getMeasuredHeight();
		if (mImageView != null)
		{
			mImageView.setImageResource(closeID);
		}
		Animation animation = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				if (interpolatedTime == 1)
				{
					v.setVisibility(View.GONE);
					isOpened = false;
				}
				else
				{
					v.getLayoutParams().height =
						initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};

		animation.setDuration(duration);
		v.startAnimation(animation);
	}

	public Boolean isOpened()
	{
		return isOpened;
	}

	public void show()
	{
		if (!isAnimationRunning)
		{
			expand(contentLayout);
			isAnimationRunning = true;
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					isAnimationRunning = false;
				}
			}, duration);
		}
	}

	public FrameLayout getHeaderLayout()
	{
		return headerLayout;
	}

	public FrameLayout getContentLayout()
	{
		return contentLayout;
	}

	public void hide()
	{
		if (!isAnimationRunning)
		{
			collapse(contentLayout);
			isAnimationRunning = true;
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					isAnimationRunning = false;
				}
			}, duration);
		}
	}

	//设置需要改变的图片指示器
	public void setImageView(ImageView mImageView, int openID, int closeID)
	{
		this.mImageView = mImageView;
		this.openID = openID;
		this.closeID = closeID;
	}

	public void setScrollView(ScrollView scrollView)
	{
		this.scrollView = scrollView;
	}
}
