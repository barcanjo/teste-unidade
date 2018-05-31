package br.com.alura.mock.testeunidade.leilao.infra.dao;

import java.util.List;

import br.com.alura.mock.testeunidade.leilao.dominio.Leilao;

public interface RepositorioDeLeiloes {

    void salva(Leilao leilao);

    List<Leilao> encerrados();

    List<Leilao> correntes();

    void atualiza(Leilao leilao);
    
}
