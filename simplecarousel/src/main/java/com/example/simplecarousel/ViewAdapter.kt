package com.example.simplecarousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.simplecarousel.R

class ViewAdapter(val list: List<Int>) : PagerAdapter() {
	
	override fun instantiateItem(container: ViewGroup, position: Int): Any {
		
		val view = LayoutInflater.from(container.context).inflate(R.layout.slide_view, container, false)
		view.findViewById<ImageView>(R.id.image).setImageResource(list[position])
		container.addView(view)
		return view
	}
	
	override fun isViewFromObject(view: View, `object`: Any): Boolean {
		return view == `object`
	}
	
	override fun getCount(): Int {
		return list.size
	}
	
	override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
		container.removeView(`object` as View)
	}
}