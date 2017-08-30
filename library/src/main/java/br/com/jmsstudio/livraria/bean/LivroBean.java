package br.com.jmsstudio.livraria.bean;

import br.com.jmsstudio.livraria.modelo.Autor;
import br.com.jmsstudio.livraria.modelo.Livro;
import br.com.jmsstudio.utils.dao.DAO;
import br.com.jmsstudio.utils.transaction.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class LivroBean implements Serializable {

	private static final long serialVersionUID = 1L;

    @Inject
	private DAO<Livro> livroDAO;

    @Inject
    private DAO<Autor> autorDAO;

	private Livro livro = new Livro();

	private Integer autorId;

	private List<Livro> livros;

	public void setAutorId(Integer autorId) {
		this.autorId = autorId;
	}

	public Integer getAutorId() {
		return autorId;
	}

	public Livro getLivro() {
		return livro;
	}

	public List<Livro> getLivros() {
		if(this.livros == null) {
			this.livros = livroDAO.listaTodos();
		}
		return livros;
	}

	public List<Autor> getAutores() {
		return autorDAO.listaTodos();
	}

	public List<Autor> getAutoresDoLivro() {
		return this.livro.getAutores();
	}

	public void carregarLivroPelaId() {
		this.livro = livroDAO.buscaPorId(this.livro.getId());
	}
	
	public void gravarAutor() {
		Autor autor = autorDAO.buscaPorId(this.autorId);
		this.livro.adicionaAutor(autor);
		System.out.println("Escrito por: " + autor.getNome());
	}

	@Transactional
	public void gravar() {
		System.out.println("Gravando livro " + this.livro.getTitulo());

		if (livro.getAutores().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage("autor",
					new FacesMessage("Livro deve ter pelo menos um Autor."));
			return;
		}

		if(this.livro.getId() == null) {
            livroDAO.adiciona(this.livro);
			this.livros = livroDAO.listaTodos();
		} else {
            livroDAO.atualiza(this.livro);
		}

		this.livro = new Livro();
	}

    @Transactional
	public void remover(Livro livro) {
		System.out.println("Removendo livro");
        livroDAO.remove(livro);
		this.livros = livroDAO.listaTodos();
	}
	
	public void removerAutorDoLivro(Autor autor) {
		this.livro.removeAutor(autor);
	}
	
	public void carregar(Livro livro) {
		System.out.println("Carregando livro");
		this.livro = livro;
	}
	
	public String formAutor() {
		System.out.println("Chamanda do formulário do Autor.");
		return "autor?faces-redirect=true";
	}

	public void comecaComDigitoUm(FacesContext fc, UIComponent component,
			Object value) throws ValidatorException {

		String valor = value.toString();
		if (!valor.startsWith("1")) {
			throw new ValidatorException(new FacesMessage(
					"ISBN deveria começar com 1"));
		}

	}
}
