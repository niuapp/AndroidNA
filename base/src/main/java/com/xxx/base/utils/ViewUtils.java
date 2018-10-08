package com.xxx.base.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class ViewUtils {
	/** 把自身从父View中移除 */
	public static View removeSelfFromParent(View view) {
		if (view != null) {
			ViewParent parent = view.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) parent;
				group.removeView(view);
			}
		}
		return view;
	}

	/** 请求View树重新布局，用于解决中层View有布局状态而导致上层View状态断裂 */
	public static void requestLayoutParent(View view, boolean isAll) {
		ViewParent parent = view.getParent();
		while (parent != null && parent instanceof View) {
			if (!parent.isLayoutRequested()) {
				parent.requestLayout();
				if (!isAll) {
					break;
				}
			}
			parent = parent.getParent();
		}
	}

	/** FindViewById的泛型封装，减少强转代码 */
	public static <T extends View> T findViewById(View layout, int id) {
		return (T) layout.findViewById(id);
	}

	/**
	 * ViewPager 滚动速度设置
	 *
	 * ViewPagerScroller scroller = new ViewPagerScroller(context);
	 * scroller.setScrollDuration(2000);
	 * scroller.initViewPagerScroll(viewPage);
	 *
	 */
	public static class ViewPagerScroller extends Scroller {
		private int mScrollDuration = 2000;             // 滑动速度

		/**
		 * 设置速度速度
		 * @param duration
		 */
		public void setScrollDuration(int duration){
			this.mScrollDuration = duration;
		}

		public ViewPagerScroller(Context context) {
			super(context);
		}

		public ViewPagerScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			super.startScroll(startX, startY, dx, dy, mScrollDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy, mScrollDuration);
		}



		public void initViewPagerScroll(ViewPager viewPage) {
			try {
				Field mScroller = ViewPager.class.getDeclaredField("mScroller");
				mScroller.setAccessible(true);
				mScroller.set(viewPage, this);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 一个点是否在View上
	 *
	 * @param view
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isInView(View view, float x, float y) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int xL = location[0];
		int yL = location[1];
		if (x < xL || x > (xL + view.getWidth()) || y < yL || y > (yL + view.getHeight())) {
			return false;
		}
		return true;
	}
}
