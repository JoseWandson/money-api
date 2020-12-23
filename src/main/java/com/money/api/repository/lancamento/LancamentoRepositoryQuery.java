package com.money.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.money.api.dto.LancamentoEstatisticaCategoria;
import com.money.api.model.Lancamento;
import com.money.api.repository.filter.LancamentoFilter;
import com.money.api.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {

	Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

	Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);

	List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mesReferencia);

}
