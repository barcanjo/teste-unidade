package br.com.alura.mock.testeunidade.leilao.servico;

import java.util.Calendar;
import java.util.List;

import br.com.alura.mock.testeunidade.leilao.dominio.Leilao;
import br.com.alura.mock.testeunidade.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilao {

	private int total = 0;
	private RepositorioDeLeiloes dao;
	private final EnviadorDeEmail carteiro;

	public EncerradorDeLeilao(RepositorioDeLeiloes dao, EnviadorDeEmail carteiro) {
        this.dao = dao;
		this.carteiro = carteiro;
    }

	public void encerra() {
		List<Leilao> todosLeiloesCorrentes = dao.correntes();

		for (Leilao leilao : todosLeiloesCorrentes) {
			try {
				if (comecouSemanaPassada(leilao)) {
					leilao.encerra();
					total++;
					dao.atualiza(leilao);
					carteiro.envia(leilao);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private boolean comecouSemanaPassada(Leilao leilao) {
		return diasEntre(leilao.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalEncerrados() {
		return total;
	}
}
