package br.com.alura.mock.testeunidade.leilao.servico;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import br.com.alura.mock.testeunidade.leilao.builder.CriadorDeLeilao;
import br.com.alura.mock.testeunidade.leilao.dominio.Leilao;
import br.com.alura.mock.testeunidade.leilao.infra.dao.LeilaoDao;
import br.com.alura.mock.testeunidade.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilaoTest {
	
	@Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {
		
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
                .naData(antiga).constroi();

        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        // criando o mock!
        LeilaoDao daoFalso = mock(LeilaoDao.class);

        // ensinando o mock a reagir da maneira que esperamos!
        when(daoFalso.correntes()).thenReturn(leiloesAntigos);

        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();

        // verificando que o metodofoi invocado ao menos 1 vez!
        verify(daoFalso, atLeastOnce()).atualiza(leilao2);

        // verificando que o metodofoi invocado ao menos 1 vez!
        verify(daoFalso, atLeast(1)).atualiza(leilao1);
        
        // verificando que o metodofoi invocado no maximo 1 vez!
        verify(daoFalso, atMost(1)).atualiza(leilao1);
        verify(daoFalso, atMost(1)).atualiza(leilao2);
        
        // garantindo que os metodos foram invocados na ordem exata
        InOrder inOrder = inOrder(daoFalso, enviadorFalso);
        
        // a primeira invocação
        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);    
        
        // a segunda invocação
        inOrder.verify(enviadorFalso, times(1)).envia(leilao1);

        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
        assertEquals(2, encerrador.getTotalEncerrados());
    }
	
	@Test
    public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {

        Calendar ontem = Calendar.getInstance();
        ontem.add(Calendar.DAY_OF_MONTH, -1);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(ontem).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(ontem).constroi();

        RepositorioDeLeiloes daoFalso = mock(LeilaoDao.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);

        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, enviadorFalso);
        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());

        // garante que o metodo atualiza nunca foi chamadao!
        verify(daoFalso, never()).atualiza(leilao1);
        verify(daoFalso, never()).atualiza(leilao2);
    }
	
	@Test
    public void deveContinuarAExecucaoMesmoQuandoDaoFalha() {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        
        // Faz o mock lancar uma excecao ao invocar o metodo atualiza para leilao1 
        doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);

        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        EncerradorDeLeilao encerrador = 
            new EncerradorDeLeilao(daoFalso, enviadorFalso);

        encerrador.encerra();

        verify(daoFalso).atualiza(leilao2);
        verify(enviadorFalso).envia(leilao2);
    }
	
	@Test
    public void deveContinuarAExecucaoMesmoQuandoEnviadorFalha() {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        
        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        
        // Faz o mock lancar uma excecao ao invocar o metodo envia para leilao1 
        doThrow(new RuntimeException()).when(enviadorFalso).envia(leilao1);

        EncerradorDeLeilao encerrador = 
            new EncerradorDeLeilao(daoFalso, enviadorFalso);

        encerrador.encerra();

        verify(daoFalso).atualiza(leilao2);
        verify(enviadorFalso).envia(leilao2);
    }

	@Test
    public void devePararAExecucaoParaTodasExcecoes() {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
            .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
            .naData(antiga).constroi();

        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        
        EnviadorDeEmail enviadorFalso = mock(EnviadorDeEmail.class);
        
        // Faz o mock lancar uma excecao ao invocar o metodo atualiza para qualquer Leilao 
        doThrow(new RuntimeException()).when(daoFalso).atualiza(any(Leilao.class));

        EncerradorDeLeilao encerrador = 
            new EncerradorDeLeilao(daoFalso, enviadorFalso);

        encerrador.encerra();
        
        // Verifica se o metodo enviar nunca foi chamado para qualquer objeto da classe Leilao
        verify(enviadorFalso, never()).envia(any(Leilao.class));
    }
}
