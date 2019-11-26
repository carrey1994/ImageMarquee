package com.example.simplecarousel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import android.view.animation.LinearInterpolator
import java.lang.reflect.Field


class CarouselViewPager : ViewPager {
	
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	
	private var scrollCarouselListener: ScrollCarouselListener? = null
	
	private var mHandler = Handler()
	lateinit var list: List<Int>
	
	private var isLock = false
	
	private var listener = object : OnPageChangeListener {
		var currentPosition = 0
		override fun onPageScrollStateChanged(state: Int) {
			if (state != SCROLL_STATE_IDLE) return
			
			if (currentPosition == 0) {
				setCurrentItem(list.lastIndex - 1, false)
			} else if (currentPosition == list.lastIndex) {
				setCurrentItem(1, false)
			}
			
		}
		
		override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
			scrollCarouselListener?.onScroll(position, positionOffset, positionOffsetPixels)
		}
		
		override fun onPageSelected(position: Int) {
			currentPosition = position
			scrollCarouselListener?.onScrollFinish(position)
		}
	}
	
	private lateinit var mScroller: Field
	private lateinit var scroller: SpeedScroller
	
	init {
		this.addOnPageChangeListener(listener)
		
		try {
			mScroller = ViewPager::class.java.getDeclaredField("mScroller")
			mScroller.isAccessible = true
			scroller = SpeedScroller(this.context, LinearInterpolator())
//			scroller.setFixedDuration(5000);
			mScroller.set(this, scroller)
		} catch (e: NoSuchFieldException) {
		} catch (e: IllegalArgumentException) {
		} catch (e: IllegalAccessException) {
		}
		
	}
	
	fun setScrollCarouselListener(scrollCarouselListener: ScrollCarouselListener) {
		this.scrollCarouselListener = scrollCarouselListener
	}
	
	interface ScrollCarouselListener {
		fun onScroll(position: Int, positionOffset: Float, positionOffsetPixels: Int)
		fun onScrollFinish(position: Int)
	}
	
	fun setData(images: List<Int>) {
		list = images
		resetHandlerTimer()
	}
	
	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(ev: MotionEvent?): Boolean {
//		if (isLock.not())
		when (ev?.action) {
			MotionEvent.ACTION_UP -> {
				resetHandlerTimer()
			}
			MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
				scroller.resetDuration()
				mHandler.removeCallbacksAndMessages(null)
			}
		}
		return super.onTouchEvent(ev)
	}
	
	private fun resetHandlerTimer() {
		mHandler.postDelayed(object : Runnable {
			private var time: Long = 0
			override fun run() {
				time += 1000
				val index = this@CarouselViewPager.currentItem + 1
				this@CarouselViewPager.setCurrentItem(index, true)
				Log.e("TimerExample", "TimerExample")
				mHandler.postDelayed(this, 5000)
			}
		}, 3000)
	}
	
	internal class SpeedScroller(context: Context?, interpolator: Interpolator?) : Scroller(context, interpolator) {
		private var mDuration = 2000
		private var mHandler = Handler()
		override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
			super.startScroll(startX, startY, dx, dy, mDuration)
		}
		
		override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
			super.startScroll(startX, startY, dx, dy, mDuration)
		}
		
		fun resetDuration() {
			mDuration = 500
			mHandler.removeCallbacksAndMessages(null)
			mHandler.postDelayed({
				mDuration = 2000
			}, 500L)
		}
		
		
	}
	
}