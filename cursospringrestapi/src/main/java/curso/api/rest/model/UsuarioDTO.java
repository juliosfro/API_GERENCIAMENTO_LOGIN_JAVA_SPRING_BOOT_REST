package curso.api.rest.model;

import java.io.Serializable;

/* Essa classe serve para restringirmos o acesso aos dados da classe principal. */
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1l;
    private String userNome;
    private String userCPF;
    private String userLogin;

    public UsuarioDTO(Usuario usuario) {
        this.userNome = usuario.getNome();
        this.userLogin = usuario.getLogin();
        this.userCPF = usuario.getCpf();
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserNome() {
        return userNome;
    }

    public void setUserNome(String userNome) {
        this.userNome = userNome;
    }

    public String getUserCPF() {
        return userCPF;
    }

    public void setUserCPF(String userCPF) {
        this.userCPF = userCPF;
    }
}
