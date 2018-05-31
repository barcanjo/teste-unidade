package br.com.alura.mock.testeunidade.leilao.infra.dao;

import br.com.alura.mock.testeunidade.leilao.dominio.Pagamento;

public interface RepositorioDePagamentos {
	
	void salva(Pagamento pagamento);
}
