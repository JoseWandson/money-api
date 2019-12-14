package com.money.api.resource;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.money.api.dto.LancamentoDTO;
import com.money.api.event.RecursoCriadoEvent;
import com.money.api.exceptionhandler.MoneyExceptionHandler.Erro;
import com.money.api.model.Lancamento;
import com.money.api.repository.filter.LancamentoFilter;
import com.money.api.service.LancamentoService;
import com.money.api.service.exception.PessoaInexistenteOuInativaException;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping
	public List<Lancamento> pesquisar(LancamentoFilter lancamentoFilter) {
		return lancamentoService.pesquisar(lancamentoFilter);
	}

	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
		return lancamentoService.buscarPeloCodigo(codigo).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody LancamentoDTO lancamentoDTO,
			HttpServletResponse response) {
		Lancamento lancamentoSalva = lancamentoService.criar(modelMapper.map(lancamentoDTO, Lancamento.class));

		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalva.getCodigo()));

		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalva);
	}

	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo) {
		lancamentoService.remover(codigo);
	}

	@ExceptionHandler({ PessoaInexistenteOuInativaException.class })
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

}