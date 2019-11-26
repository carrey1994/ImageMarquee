package com.example.simplecarousel

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout


class ViewPagerFrame : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private lateinit var imageList: List<Int>

    private val dotLayout = LinearLayout(context)
    lateinit var dots: List<ImageCarouselFrame.CircleDotView>
    private lateinit var viewpager: CarouselViewPager

    fun setData(images: List<Int>) {
        imageList = arrayListOf<Int>().apply {
            add(images.last())
            addAll(images)
            add(images.first())
        }

        viewpager = CarouselViewPager(context)
        viewpager.adapter = ViewAdapter(imageList)
        viewpager.setData(imageList)

        viewpager.setScrollCarouselListener(object : CarouselViewPager.ScrollCarouselListener {
            var sumOffset = 0f
            override fun onScrollFinish(position: Int) {
                Log.e("POSITION===", "$position===")
            }

            override fun onScroll(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position + positionOffset < sumOffset) {
                    //Swipe to left
                    if (viewpager.currentItem == position && positionOffsetPixels == 0) return

                    val scaleIndex = if (position - 1 < 0) dots.lastIndex else position - 1
                    val shrinkIndex = if (scaleIndex == dots.lastIndex) 0 else scaleIndex + 1
                    val level = 1 + (1 - positionOffset)
                    val shrinkLevel = 2 - (1 - positionOffset)
                    val fadeOut =
                        getAlphaFade(dots[scaleIndex], level / 2 + 0.05f, level / 2 + 0.05f)

                    val scaleX = getAniScaleX(dots[scaleIndex], 1f, level)
                    val scaleY = getAniScaleY(dots[scaleIndex], 1f, level)

                    val aniScale = AnimatorSet()
                    aniScale.playTogether(scaleX, scaleY, fadeOut)
                    aniScale.start()

                    val shrinkX = getAniScaleX(dots[shrinkIndex], shrinkLevel, shrinkLevel)
                    val shrinkY = getAniScaleY(dots[shrinkIndex], shrinkLevel, shrinkLevel)
                    val fadeIn = getAlphaFade(
                        dots[shrinkIndex],
                        0.5f + 0.5f * positionOffset,
                        0.5f + 0.5f * positionOffset
                    )
                    Log.e("FadeLeft=====", "${level / 2}")
                    val aniShrink = AnimatorSet()
                    aniShrink.playTogether(shrinkX, shrinkY, fadeIn)
                    aniShrink.start()

                } else {
                    //Swipe to right
                    if (viewpager.currentItem == position && positionOffsetPixels == 0) return
                    val scaleIndex = if (position > dots.lastIndex) 0 else position
                    val shrinkIndex = if (scaleIndex - 1 >= 0) scaleIndex - 1 else dots.lastIndex
                    val level = 1 + positionOffset
                    val shrinkLevel = 2 - positionOffset

                    val scaleX = getAniScaleX(dots[scaleIndex], 1f, level)
                    val scaleY = getAniScaleY(dots[scaleIndex], 1f, level)
                    val fadeOut =
                        getAlphaFade(dots[scaleIndex], level / 2 + 0.05f, level / 2 + 0.05f)

                    val aniScale = AnimatorSet()
                    aniScale.playTogether(scaleX, scaleY, fadeOut)
                    aniScale.start()
                    Log.e("FadeRight=====", "${level / 2}")
                    val shrinkX = getAniScaleX(dots[shrinkIndex], shrinkLevel, shrinkLevel)
                    val shrinkY = getAniScaleY(dots[shrinkIndex], shrinkLevel, shrinkLevel)
                    val fadeIn = getAlphaFade(
                        dots[shrinkIndex],
                        1f - 0.5f * positionOffset,
                        1f - 0.5f * positionOffset
                    )

                    val aniShrink = AnimatorSet()
                    aniShrink.playTogether(shrinkX, shrinkY, fadeIn)
                    aniShrink.start()
                }
                sumOffset = position + positionOffset
            }
        })

        addView(viewpager)

        viewpager.currentItem = 1

        dots = imageList.dropLast(1).drop(1).map { ImageCarouselFrame.CircleDotView(context) }
        dots.forEach {
            it.alpha = 0.5f
            dotLayout.addView(it)
        }

        val scaleX = getAniScaleX(dots[0], 1f, 2f)
        val scaleY = getAniScaleY(dots[0], 1f, 2f)
        val fadeOut = getAlphaFade(dots[0], 15f, 1f)
        val aniSetShrink = AnimatorSet()
        aniSetShrink.playTogether(scaleX, scaleY, fadeOut)
        aniSetShrink.start()


        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val mGravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        params.gravity = mGravity
        params.bottomMargin = 30
        dotLayout.layoutParams = params


        addView(dotLayout)


    }

    private fun getAniScaleX(view: View, start: Float, end: Float) =
        ObjectAnimator.ofFloat(view, "ScaleX", start, end).apply { duration = 0L }

    private fun getAniScaleY(view: View, start: Float, end: Float) =
        ObjectAnimator.ofFloat(view, "ScaleY", start, end).apply { duration = 0L }

    private fun getAlphaFade(view: View, start: Float, end: Float) =
        ObjectAnimator.ofFloat(view, "alpha", start, end).apply { duration = 0L }


    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val frameWidth = MeasureSpec.getSize(widthMeasureSpec)
        val frameHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (::dots.isInitialized)
            dots.forEach {
                it.run { layoutParams = LinearLayout.LayoutParams(50, 50) }
            }

    }


}
	
	
