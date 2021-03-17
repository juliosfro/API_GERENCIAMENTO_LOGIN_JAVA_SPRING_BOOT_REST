package curso.api.rest.model;

import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* Essa classe serve para restringirmos o acesso aos dados da classe principal. */
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1l;
    private Long id;
    private String nome;
    private String login;
    private String cpf;
    private String senha;
    private List<Telefone> telefones = new ArrayList<Telefone>();

    public UsuarioDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.login = usuario.getLogin();
        this.cpf = usuario.getCpf();
        this.id = usuario.getId();
        this.senha = usuario.getSenha();
        this.telefones = usuario.getTelefones();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

}
