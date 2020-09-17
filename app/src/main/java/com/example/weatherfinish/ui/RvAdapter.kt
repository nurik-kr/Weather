package com.example.weatherfinish.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherfinish.R
import com.example.weatherfinish.model.forecast.DayItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_forecast.view.*

class RvAdapter : RecyclerView.Adapter<RvHolder>() {

    val list = arrayListOf<DayItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return RvHolder(view)
    }

    fun update(list: List<DayItem>?) {
        if (list != null) {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RvHolder, position: Int) {
        holder.bind(list[position])
    }
}

class RvHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(dayItem: DayItem) {

        val image = dayItem.weather.first().icon
        Picasso.get().load("http://openweathermap.org/img/w/$image.png").into(itemView.cloudy)

        itemView.tvRvTemp.text = itemView.context.getString(R.string.degreeformat,dayItem.temp.day.toInt().toString())
        itemView.tvRvMaxTemp.text = itemView.context.getString(R.string.degreeformat,dayItem.temp.max.toInt().toString())
        itemView.tvRvMinTemp.text = itemView.context.getString(R.string.degreeformat,dayItem.temp.min.toInt().toString())

    }
}