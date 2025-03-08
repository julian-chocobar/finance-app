package com.thames.finance_app.mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.PagoRequest;
import com.thames.finance_app.dtos.PagoResponse;
import com.thames.finance_app.enums.TipoPago;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Pago;

@Component
public class PagoMapper {
	
	public Pago toEntity (PagoRequest request) {
		return Pago.builder()
				.fecha(request.getFecha())
				.tipoEntrega(request.getTipoEntrega())
				.valor(request.getValor())
				.build();
	}
	
  public Pago toPago(PagoRequest request, TipoPago tipoPago) {
  return Pago.builder()
          .fecha(request.getFecha())
          .tipoEntrega(request.getTipoEntrega())
          .valor(request.getValor())
          .tipoPago(tipoPago)
          .build();
  }
  
  public List<PagoResponse> toPagoResponseList(List<Pago> pagos) {
      if (pagos == null) return Collections.emptyList();
      return pagos.stream().map(this::toPagoResponse).collect(Collectors.toList());
  }

  public PagoResponse toPagoResponse(Pago pago) {
      return PagoResponse.builder()
              .id(pago.getId())
              .fecha(pago.getFecha())
              .tipoEntrega(pago.getTipoEntrega().name())
              .valor(pago.getValor())
              .build();
  }

  public List<Pago> toPagoList(List<PagoRequest> pagosRequest, TipoPago tipoPago) {
      if (pagosRequest == null) return new ArrayList<>();
      return pagosRequest.stream()
              .map(pagoReq -> toPago(pagoReq, tipoPago))
              .collect(Collectors.toList());
  }
  
  public void vincularConOperacion(Pago pago, Operacion operacion) {
	  
  }

}
