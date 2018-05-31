package br.com.alura.mock.testeunidade.leilao.servico;

import br.com.alura.mock.testeunidade.leilao.dominio.Leilao;

public interface EnviadorDeEmail {
	
	void envia(Leilao leilao);
}
