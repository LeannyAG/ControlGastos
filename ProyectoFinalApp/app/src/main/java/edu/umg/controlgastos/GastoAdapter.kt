package edu.umg.controlgastos


    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.recyclerview.widget.RecyclerView
    import edu.umg.controlgastos.databinding.ItemGastoBinding
    import edu.umg.controlgastos.modelo.Gasto

    class GastoAdapter(
        private var gastos: List<Gasto>,
        private val onGastoClick: (Gasto) -> Unit
    ) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

        inner class GastoViewHolder(
            val binding: ItemGastoBinding
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): GastoViewHolder {
            val binding = ItemGastoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return GastoViewHolder(binding)
        }

        override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
            val gasto = gastos[position]

            holder.binding.txtDescripcion.text = gasto.descripcion
            holder.binding.txtCategoria.text = gasto.tipoGasto.nombre
            holder.binding.txtMonto.text = "Q %.2f".format(gasto.monto)

            holder.binding.root.setOnClickListener {
                onGastoClick(gasto)
            }
        }

        override fun getItemCount(): Int = gastos.size

        fun actualizarLista(nuevosGastos: List<Gasto>) {
            gastos = nuevosGastos
            notifyDataSetChanged()
        }
    }
