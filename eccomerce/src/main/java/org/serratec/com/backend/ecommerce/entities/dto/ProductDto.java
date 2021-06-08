package org.serratec.com.backend.ecommerce.entities.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProductDto {

	@NotNull
	private LocalDate dataCadastro = LocalDate.now();

	@NotBlank
	@Size(max = 40)
	private String nome;

	@Size(max = 250)
	private String descricao;

	@NotBlank
	private Double preco;

	@NotBlank
	private Integer quantidadeEstoque;

	@NotBlank
	private Long categoria;

	public LocalDate getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDate dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}

	public Integer getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(Integer quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

	public Long getCategoria() {
		return categoria;
	}

	public void setCategoria(Long categoria) {
		this.categoria = categoria;
	}

	@Override
	public String toString() {
		return "ProductDto [dataCadastro=" + dataCadastro + ", nome=" + nome + ", descricao=" + descricao + ", preco="
				+ preco + ", quantidadeEstoque=" + quantidadeEstoque + ", categoria=" + categoria + "]";
	}
	
	// Adicionar imagens
	// private String imagem;
	
}
