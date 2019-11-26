package com.example.simplecarousel

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import java.lang.Exception
import kotlin.math.min

class ImageCarouselFrame : FrameLayout {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
		context,
		attrs,
		defStyleAttr
	)
	
	private val dotLayout = LinearLayout(context)
	lateinit var dots: List<CircleDotView>
	
	fun setData(bitmaps: List<Bitmap>) {
		val carousel = ImageCarousel(context).apply {
			setData(bitmaps)
			setAnimationDrawListener(object : ImageCarousel.AnimationDrawListener {
				
				override fun onStartIndex(index: Int) {
					try {
						val scaleX = getAniScaleX(dots[index + 1], 1f, 1.7f)
						val scaleY = getAniScaleY(dots[index + 1], 1f, 1.7f)
						val aniScale = AnimatorSet()
						aniScale.playTogether(scaleX, scaleY)
						aniScale.start()
						
						val shrinkX = getAniScaleX(dots[index], 1.7f, 1f)
						val shrinkY = getAniScaleY(dots[index], 1.7f, 1f)
						val aniShrink = AnimatorSet()
						aniShrink.playTogether(shrinkX, shrinkY)
						aniShrink.start()
						
					} catch (e: Exception) {
						val scaleX = getAniScaleX(dots.first(), 1f, 1.7f)
						val scaleY = getAniScaleY(dots.first(), 1f, 1.7f)
						val aniSetShrink = AnimatorSet()
						aniSetShrink.playTogether(scaleX, scaleY)
						aniSetShrink.start()
						
						val shrinkX = getAniScaleX(dots.last(), 1.7f, 1f)
						val shrinkY = getAniScaleY(dots.last(), 1.7f, 1f)
						val aniShrink = AnimatorSet()
						aniShrink.playTogether(shrinkX, shrinkY)
						aniShrink.start()
					}
				}
				
			})
		}
		
		
		addView(carousel)
		
		
		dots = bitmaps.map { CircleDotView(context) }
		dots.forEach {
			dotLayout.addView(it)
		}
		
		val scaleX = getAniScaleX(dots.first(), 1f, 1.7f)
		val scaleY = getAniScaleY(dots.first(), 1f, 1.7f)
		val aniSetShrink = AnimatorSet()
		aniSetShrink.playTogether(scaleX, scaleY)
		aniSetShrink.start()
		
		
		val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
		val mGravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
		params.gravity = mGravity
		params.bottomMargin = 30
		dotLayout.layoutParams = params
		
		
		
		addView(dotLayout)
		invalidate()
	}
	
	
	@SuppressLint("DrawAllocation")
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		
		MeasureSpec.getSize(widthMeasureSpec)
		MeasureSpec.getSize(heightMeasureSpec)
		dots.forEach {
			it.run { layoutParams = LinearLayout.LayoutParams(50, 50) }
		}
		
	}
	
	
	private fun getAniScaleX(view: View, start: Float, end: Float) =
		ObjectAnimator.ofFloat(view, "ScaleX", start, end).apply { duration = 1000L }
	
	private fun getAniScaleY(view: View, start: Float, end: Float) =
		ObjectAnimator.ofFloat(view, "ScaleY", start, end).apply { duration = 1000L }
	
	class CircleDotView : View {
		constructor(context: Context?) : super(context)
		constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
		constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
			context,
			attrs,
			defStyleAttr
		)
		
		private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
			strokeWidth = 1f
			style = Paint.Style.FILL
			color = Color.WHITE
			strokeCap = Paint.Cap.ROUND
		}
		
		override fun onDraw(canvas: Canvas?) {
			super.onDraw(canvas)
			val radius = min(width, height)
			canvas?.drawCircle(radius / 2f, radius / 2f, radius / 6f, paint)
		}
	}
}