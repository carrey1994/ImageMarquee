package com.example.simplecarousel

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart


class ImageCarousel : HorizontalScrollView {
	
	private var animationDrawListener: AnimationDrawListener? = null
	
	constructor(context: Context?) : super(context)

	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
	
	private val imageLayout = LinearLayout(context)
	private val mDuration = 1000L
	private lateinit var images: ArrayList<ImageView>
	private var isStart = true
	private var isFirstTime = true
	
	init {
		isVerticalScrollBarEnabled = false
		isHorizontalScrollBarEnabled = false
	}
	
	fun setData(bitmaps: List<Bitmap>) {
		images = arrayListOf<ImageView>()
		val originalBitmap = bitmaps.map {
			ImageView(context).apply {
				setImageBitmap(it)
				scaleType = ImageView.ScaleType.CENTER_CROP
			}
		}
		images.addAll(originalBitmap)
		images.add(ImageView(context).apply {
			setImageBitmap(bitmaps[0])
			scaleType = ImageView.ScaleType.CENTER_CROP
		})
		
		images.forEach { imageLayout.addView(it) }
		
		addView(imageLayout)
		invalidate()
	}
	
//	@SuppressLint("ClickableViewAccessibility")
//	override fun onTouchEvent(ev: MotionEvent?): Boolean {
//		return true
//	}
	
	@SuppressLint("DrawAllocation")
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val carouselWidth = MeasureSpec.getSize(widthMeasureSpec)
		val carouselHeight = MeasureSpec.getSize(heightMeasureSpec)
		images.forEach {
			it.run { layoutParams = LinearLayout.LayoutParams(carouselWidth, carouselHeight) }
		}
	}
	
	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		if (isStart) {
			when (isFirstTime) {
				true -> {
					for (index in 0 until images.lastIndex) {
						if (index != images.lastIndex - 1) {
							animPostDelay(this@ImageCarousel.width * (index + 1), index,
								{ animationDrawListener?.onStartIndex(index) }
							)
						} else {
							animOnEndReset(this@ImageCarousel.width * (index + 1), index,
								{ animationDrawListener?.onStartIndex(index) },
								{
									scrollTo(0, 0)
									isStart = true
									isFirstTime = false
								}
							)
						}
					}
				}
				false -> {
					for (index in 0 until images.lastIndex) {
						if (index == images.lastIndex - 1) {
							animOnEndReset(this@ImageCarousel.width * (index + 1), index,
								{ animationDrawListener?.onStartIndex(index) }, {
								scrollTo(0, 0)
								isStart = true
							}
							)
						} else {
							animPostDelay(this@ImageCarousel.width * (index + 1), index,
								{ animationDrawListener?.onStartIndex(index) }
							)
						}
					}
				}
			}
			isStart = false
		}
	}
	
	private fun getOddDuration(times: Int) = mDuration * (2 * times + 1)
	
	private fun animPostDelay(distance: Int, durationTimes: Int, startFn: (() -> Unit)? = null, endFn: (() -> Unit)? = null) {
		Handler().postDelayed({
			val anim = ObjectAnimator.ofInt(this, "scrollX", distance)
			anim.duration = mDuration
			anim.doOnEnd { endFn?.invoke() }
			anim.doOnStart { startFn?.invoke() }
			anim.start()
		}, getOddDuration(durationTimes))
	}
	
	private fun animOnEndReset(distance: Int, durationTimes: Int, startFn: (() -> Unit)? = null, endFn: (() -> Unit)? = null) {
		Handler().postDelayed({
			val anim = ObjectAnimator.ofInt(this, "scrollX", distance)
			anim.duration = mDuration
			anim.doOnEnd { endFn?.invoke() }
			anim.doOnStart { startFn?.invoke() }
			anim.start()
		}, getOddDuration(durationTimes))
	}
	
	interface AnimationDrawListener {
		fun onStartIndex(index: Int)
	}
	
	fun setAnimationDrawListener(animationDrawListener: AnimationDrawListener) {
		this.animationDrawListener = animationDrawListener
	}
	
}