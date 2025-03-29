package com.thames.finance_app.mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.PagoDTO;
import com.thames.finance_app.enums.TipoPago;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Pago;

@Component
public class PagoMapper {

	public Pago toEntity (PagoDTO dto) {
		return Pago.builder()
				.fecha(dto.getFecha())
				.tipoEntrega(dto.getTipoEntrega())
				.valor(dto.getValor())
				.build();
	}

  public Pago toPago(PagoDTO dto, TipoPago tipoPago) {
  return Pago.builder()
          .fecha(dto.getFecha())
          .tipoEntrega(dto.getTipoEntrega())
          .valor(dto.getValor())
          .tipoPago(tipoPago)
          .build();
  }

  public List<PagoDTO> toPagoResponseList(List<Pago> pagos) {
      if (pagos == null) {
		return Collections.emptyList();
	}
      return pagos.stream().map(this::toDTO).collect(Collectors.toList());
  }

  public PagoDTO toDTO(Pago pago) {
      return PagoDTO.builder()
              .id(pago.getId())
              .fecha(pago.getFecha())
              .tipoEntrega(pago.getTipoEntrega())
              .valor(pago.getValor())
              .build();
  }

  public List<Pago> toPagoList(List<PagoDTO> pagosRequest, TipoPago tipoPago) {
      if (pagosRequest == null) {
		return new ArrayList<>();
	}
      return pagosRequest.stream()
              .map(pagoReq -> toPago(pagoReq, tipoPago))
              .collect(Collectors.toList());
  }

  public void vincularConOperacion(Pago pago, Operacion operacion) {

  }



}
