package bte.sgrc.SpringBackend.api.security.jwt;

import java.io.Serializable;

public class JwtAuthenticationRequest implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String email;
    private String password;

    public JwtAuthenticationRequest(){
        super();
    }

    public JwtAuthenticationRequest(String email, String password){
        this.setEmail(email);
        this.setPassword(password);
    }

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}