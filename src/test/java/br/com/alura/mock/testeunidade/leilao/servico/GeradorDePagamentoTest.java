package br.com.alura.mock.testeunidade.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.alura.mock.testeunidade.leilao.builder.CriadorDeLeilao;
import br.com.alura.mock.testeunidade.leilao.dominio.Leilao;
import br.com.alura.mock.testeunidade.leilao.dominio.Pagamento;
import br.com.alura.mock.testeunidade.leilao.dominio.Usuario;
import br.com.alura.mock.testeunidade.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.alura.mock.testeunidade.leilao.infra.dao.RepositorioDePagamentos;
import br.com.alura.mock.testeunidade.leilao.relogio.Relogio;

public class GeradorDePagamentoTest {

	@Test
	public void deveGerarPagamentoParaUmLeilaoEncerrado() {

		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Avaliador avaliador = new Avaliador();

		Leilao leilao = new CriadorDeLeilao().para("Playstation").lance(new Usuario("José da Silva"), 2000.0)
				.lance(new Usuario("Maria Pereira"), 2500.0).constroi();

		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, avaliador);
		gerador.gera();

		// criamos o ArgumentCaptor que sabe capturar um Pagamento
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);

		// capturamos o Pagamento que foi passado para o método salvar
		verify(pagamentos).salva(argumento.capture());

		// Recupera o pagamento salvo atraves do ArgumentCaptor
		Pagamento pagamentoGerado = argumento.getValue();

		// garante que o pagamento salvo foi de valor 2500
		assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
	}

	@Test
	public void deveEmpurrarParaOProximoDiaUtilPorHojeSerUmSabado() {

		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		// dia 7/abril/2012 um sabado
		Calendar sabado = Calendar.getInstance();
		sabado.set(2012, Calendar.APRIL, 7);
		
		// ensinamos o mock a dizer que "hoje" equivale a um sabado
		when(relogio.hoje()).thenReturn(sabado);

		Leilao leilao = new CriadorDeLeilao()
				.para("Playstation")
				.lance(new Usuario("José da Silva"), 2000.0)
				.lance(new Usuario("Maria Pereira"), 2500.0)
				.constroi();

		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(), relogio);
		gerador.gera();
		
		// criamos o ArgumentCaptor que sabe capturar um Pagamento
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		
		// Recupera o pagamento salvo atravoes do ArgumentCaptor
		verify(pagamentos).salva(argumento.capture());
		
		// Recupera o pagamento salvo atraves do ArgumentCaptor
		Pagamento pagamentoGerado = argumento.getValue();
		
		// garante que segunda-feira equivale ao dia do pagamento gerado
		assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void deveEmpurrarParaOProximoDiaUtilPorHojeSerUmDoming() {

		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		// dia 8/abril/2012 um domingo
		Calendar sabado = Calendar.getInstance();
		sabado.set(2012, Calendar.APRIL, 8);
		
		// ensinamos o mock a dizer que "hoje" equivale a um domingo
		when(relogio.hoje()).thenReturn(sabado);

		Leilao leilao = new CriadorDeLeilao()
				.para("Playstation")
				.lance(new Usuario("José da Silva"), 2000.0)
				.lance(new Usuario("Maria Pereira"), 2500.0)
				.constroi();

		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(), relogio);
		gerador.gera();
		
		// criamos o ArgumentCaptor que sabe capturar um Pagamento
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		
		// Recupera o pagamento salvo atravoes do ArgumentCaptor
		verify(pagamentos).salva(argumento.capture());
		
		// Recupera o pagamento salvo atraves do ArgumentCaptor
		Pagamento pagamentoGerado = argumento.getValue();
		
		// garante que segunda-feira equivale ao dia do pagamento gerado
		assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
	}
}