package com.treel.androidsdkdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.treel.androidsdk.database.TpmsDetectionData
import com.treel.androidsdk.utility.Utility
import kotlinx.android.synthetic.main.item_view_tpms_data.view.*

class TpmsDataAdapter (private var vehicleList: List<TpmsDetectionData>) :
    RecyclerView.Adapter<TpmsDataAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        type: Int
    ): TpmsDataAdapter.TaskViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_tpms_data, parent, false)


        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(
        viewHolder: TpmsDataAdapter.TaskViewHolder,
        position: Int
    ) {
        viewHolder.bind(vehicleList[position], position)
    }

    override fun getItemCount(): Int = vehicleList.size


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(scanTpmsData: TpmsDetectionData, position: Int) = with(itemView) {
           val detectedTime = Utility.getTimeFromDateTimeFormat(scanTpmsData.timeStamp) ?: "-"
            itemView.textViewVinNumber.text = scanTpmsData.vinNumber
            itemView.textViewTyrePosition.text = "${scanTpmsData.tyrePosition}"
            itemView.textViewPressure.text = "${scanTpmsData.pressure}"
            itemView.textViewTemperature.text = "${scanTpmsData.temperaure}"

            itemView.textViewBattery.text = "${scanTpmsData.battery}"
            itemView.textViewTimeStamp.text = "${detectedTime}"

        }
    }
}