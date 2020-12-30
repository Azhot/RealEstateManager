package fr.azhot.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.azhot.realestatemanager.databinding.CellPointOfInterestBinding
import fr.azhot.realestatemanager.model.PointOfInterest

class PointOfInterestListAdapter(pointOfInterestList: MutableList<PointOfInterest>) :
    RecyclerView.Adapter<PointOfInterestListAdapter.PointOfInterestViewHolder>() {


    // variables
    var pointOfInterestList: MutableList<PointOfInterest> = pointOfInterestList
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointOfInterestViewHolder {
        val view = CellPointOfInterestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointOfInterestViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointOfInterestViewHolder, position: Int) =
        holder.bind(pointOfInterestList[position])

    override fun getItemCount(): Int = pointOfInterestList.count()


    // inner class
    class PointOfInterestViewHolder(private val binding: CellPointOfInterestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pointOfInterest: PointOfInterest) {
            binding.pointOfInterest.text = StringBuilder().run {
                append(pointOfInterest.toString())
                toString()
            }
        }
    }
}

