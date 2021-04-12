package curso.api.rest.model;

import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
    private Date dataNascimento;
    private Profissao profissao;
    private BigDecimal salario;

    public UsuarioDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.login = usuario.getLogin();
        this.cpf = usuario.getCpf();
        this.id = usuario.getId();
        this.senha = usuario.getSenha();
        this.telefones = usuario.getTelefones();
        this.dataNascimento = usuario.getDataNascimento();
        this.profissao = usuario.getProfissao();
        this.salario = usuario.getSalario();
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

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public void setProfissao(Profissao profissao) {
        this.profissao = profissao;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public BigDecimal getSalario() {
        return salario;
    }

}
