package org.serratec.com.backend.ecommerce.entities.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AddressDto {

	@NotEmpty(message = "Este campo não pode ser vazio")
	@Size(min = 9, max = 9)
	private String cep;

	@Column(name = "rua")
	private String logradouro;

	private String bairro;

	@Column(name = "cidade")
	private String localidade;

	@NotEmpty(message = "Este campo não pode ser vazio")
	private String numero;

	@Size(max = 120)
	private String complemento;

	@Column(name = "estado")
	private String uf;

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}
