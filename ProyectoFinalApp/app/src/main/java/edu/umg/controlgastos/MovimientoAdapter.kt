package edu.umg.controlgastos
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class MovimientoAdapter(
    private var movimientos: List<Movimiento>
) : RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    class MovimientoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val icono: TextView =
            itemView.findViewById(R.id.txtIconoMovimiento)

        val descripcion: TextView =
            itemView.findViewById(R.id.txtDescripcionMovimiento)

        val detalle: TextView =
            itemView.findViewById(R.id.txtDetalleMovimiento)

        val monto: TextView =
            itemView.findViewById(R.id.txtMontoMovimiento)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovimientoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)

        return MovimientoViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: MovimientoViewHolder,
        position: Int
    ) {
        val movimiento = movimientos[position]

        holder.descripcion.text = movimiento.descripcion
        holder.detalle.text =
            "${movimiento.categoria} • ${movimiento.fecha}"

        if (movimiento.esIngreso) {
            holder.icono.text = "↙"
            holder.icono.setTextColor(0xFF177354.toInt())
            holder.monto.setTextColor(0xFF177354.toInt())
            holder.monto.text = String.format(
                Locale.US,
                "+ Q %,.2f",
                movimiento.monto
            )
        } else {
            holder.icono.text = "↗"
            holder.icono.setTextColor(0xFFBD453E.toInt())
            holder.monto.setTextColor(0xFFBD453E.toInt())
            holder.monto.text = String.format(
                Locale.US,
                "- Q %,.2f",
                movimiento.monto
            )
        }
    }

    override fun getItemCount(): Int = movimientos.size

    fun actualizarLista(nuevaLista: List<Movimiento>) {
        movimientos = nuevaLista
        notifyDataSetChanged()
    }
}