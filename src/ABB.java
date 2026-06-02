import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K, V> implements IMapeamento<K, V>{

	private No<K, V> raiz; // referência à raiz da árvore.
	private Comparator<K> comparador; //comparador empregado para definir "menores" e "maiores".
	private int tamanho;
	private long comparacoes;
	private long inicio;
	private long termino;
	
	/**
	 * Método auxiliar para inicialização da árvore binária de busca.
	 * 
	 * Este método define a raiz da árvore como {@code null} e seu tamanho como 0.
	 * Utiliza o comparador fornecido para definir a organização dos elementos na árvore.
	 * @param comparador o comparador para organizar os elementos da árvore.
	 */
	private void init(Comparator<K> comparador) {
		raiz = null;
		tamanho = 0;
		this.comparador = comparador;
	}

	/**
	 * Construtor da classe.
	 * O comparador padrão de ordem natural será utilizado.
	 */ 
	@SuppressWarnings("unchecked")
	public ABB() {
	    init((Comparator<K>) Comparator.naturalOrder());
	}

	/**
	 * Construtor da classe.
	 * Esse construtor cria uma nova árvore binária de busca vazia.
	 *  
	 * @param comparador o comparador a ser utilizado para organizar os elementos da árvore.  
	 */
	public ABB(Comparator<K> comparador) {
	    init(comparador);
	}

    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária de busca a partir de uma outra árvore binária de busca,
     * com os mesmos itens, mas usando uma nova chave.
     * @param original a árvore binária de busca original.
     * @param funcaoChave a função que irá extrair a nova chave de cada item para a nova árvore.
     */
    @SuppressWarnings("unchecked")
	public ABB(ABB<?, V> original, Function<V, K> funcaoChave) {
        ABB<K, V> nova = new ABB<>();
        nova = copiarArvore(original.raiz, funcaoChave, nova);
        this.raiz = nova.raiz;
        this.comparador = (Comparator<K>) Comparator.naturalOrder();
    }
    
    /**
     * Recursivamente, copia os elementos da árvore original para esta, num processo análogo ao caminhamento em ordem.
     * @param <T> Tipo da nova chave.
     * @param raizArvore raiz da árvore original que será copiada.
     * @param funcaoChave função extratora da nova chave para cada item da árvore.
     * @param novaArvore Nova árvore. Parâmetro usado para permitir o retorno da recursividade.
     * @return A nova árvore com os itens copiados e usando a chave indicada pela função extratora.
     */
    private <T> ABB<T, V> copiarArvore(No<?, V> raizArvore, Function<V, T> funcaoChave, ABB<T, V> novaArvore) {
    	
        if (raizArvore != null) {
    		novaArvore = copiarArvore(raizArvore.getEsquerda(), funcaoChave, novaArvore);
            V item = raizArvore.getItem();
            T chave = funcaoChave.apply(item);
    		novaArvore.inserir(chave, item);
    		novaArvore = copiarArvore(raizArvore.getDireita(), funcaoChave, novaArvore);
    	}
        return novaArvore;
    }
    
    /**
	 * Método booleano que indica se a árvore está vazia ou não.
	 * @return
	 * verdadeiro: se a raiz da árvore for null, o que significa que a árvore está vazia.
	 * falso: se a raiz da árvore não for null, o que significa que a árvore não está vazia.
	 */
	public Boolean vazia() {
	    return (this.raiz == null);
	}
    
    @Override
    /**
     * Método que encapsula a pesquisa recursiva de itens na árvore.
     * @param chave a chave do item que será pesquisado na árvore.
     * @return o valor associado à chave.
     */
	public V pesquisar(K chave) {
    	comparacoes = 0;
    	inicio = System.nanoTime();
    	V procurado = pesquisar(raiz, chave);
    	termino = System.nanoTime();
    	return procurado;
	}
    
    private V pesquisar(No<K, V> raizArvore, K procurado) {
    	
    	int comparacao;
    	
    	comparacoes++;
    	if (raizArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(procurado, raizArvore.getChave());
    	
    	if (comparacao == 0)
    		/// O item procurado foi encontrado.
    		return raizArvore.getItem();
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		return pesquisar(raizArvore.getEsquerda(), procurado);
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		return pesquisar(raizArvore.getDireita(), procurado);
    }
    
    @Override
    /**
     * Método que encapsula a adição recursiva de itens à árvore, associando-o à chave fornecida.
     * @param chave a chave associada ao item que será inserido na árvore.
     * @param item o item que será inserido na árvore.
     * 
     * @return o tamanho atualizado da árvore após a execução da operação de inserção.
     */
    public int inserir(K chave, V item) {
    	raiz = inserir(raiz, chave, item);
    	return tamanho;
    }

    /**
     * Insere recursivamente um item na (sub-)árvore, mantendo a propriedade de ordenação da ABB.
     * @param raizArvore raiz da (sub-)árvore onde o item será inserido.
     * @param chave a chave associada ao item.
     * @param item o item que será inserido.
     * @return a raiz da (sub-)árvore após a inserção.
     */
    private No<K, V> inserir(No<K, V> raizArvore, K chave, V item) {

    	if (raizArvore == null) {
    		raizArvore = new No<>(chave, item);
    		tamanho++;
    	} else {
    		int comparacao = comparador.compare(chave, raizArvore.getChave());

    		if (comparacao < 0)
    			raizArvore.setEsquerda(inserir(raizArvore.getEsquerda(), chave, item));
    		else if (comparacao > 0)
    			raizArvore.setDireita(inserir(raizArvore.getDireita(), chave, item));
    		else
    			throw new RuntimeException("A chave já existe na árvore!");
    	}

    	return raizArvore;
    }

    @Override
    public String toString(){
    	return percorrer();
    }

    @Override
    public String percorrer() {
    	StringBuilder percurso = new StringBuilder();
    	percorrer(raiz, percurso);
    	return percurso.toString();
    }

    /**
     * Percorre recursivamente a árvore em ordem (esquerda, raiz, direita), acumulando
     * os itens visitados em ordem crescente de chave.
     * @param raizArvore raiz da (sub-)árvore visitada.
     * @param percurso acumulador da representação textual dos itens.
     */
    private void percorrer(No<K, V> raizArvore, StringBuilder percurso) {

    	if (raizArvore != null) {
    		percorrer(raizArvore.getEsquerda(), percurso);
    		percurso.append(raizArvore.getItem() + "\n");
    		percorrer(raizArvore.getDireita(), percurso);
    	}
    }

    @Override
    /**
     * Método que encapsula a remoção recursiva de um item da árvore.
     * @param chave a chave do item que deverá ser localizado e removido da árvore.
     * @return o valor associado ao item removido.
     */
    public V remover(K chave) {
    	V removido = pesquisar(chave);
    	raiz = remover(raiz, chave);
    	return removido;
    }

    /**
     * Remove recursivamente o item de chave indicada da (sub-)árvore, preservando a
     * propriedade de ordenação da ABB.
     *
     * Em nós com dois filhos, a chave/item a remover é substituída pelo seu antecessor
     * (maior chave da sub-árvore esquerda), que em seguida é removido dessa sub-árvore.
     * @param raizArvore raiz da (sub-)árvore de onde o item será removido.
     * @param chave a chave do item a remover.
     * @return a raiz da (sub-)árvore após a remoção.
     */
    private No<K, V> remover(No<K, V> raizArvore, K chave) {

    	if (raizArvore == null)
    		throw new NoSuchElementException("O item não foi localizado na árvore!");

    	int comparacao = comparador.compare(chave, raizArvore.getChave());

    	if (comparacao < 0)
    		raizArvore.setEsquerda(remover(raizArvore.getEsquerda(), chave));
    	else if (comparacao > 0)
    		raizArvore.setDireita(remover(raizArvore.getDireita(), chave));
    	else if (raizArvore.getEsquerda() == null) {
    		raizArvore = raizArvore.getDireita();
    		tamanho--;
    	} else if (raizArvore.getDireita() == null) {
    		raizArvore = raizArvore.getEsquerda();
    		tamanho--;
    	} else
    		raizArvore = substituir(raizArvore, raizArvore.getEsquerda());

    	return raizArvore;
    }

    /**
     * Localiza o antecessor (nó de maior chave) da sub-árvore esquerda, copia sua chave e
     * item para o nó que está sendo removido e elimina o antecessor de sua posição original.
     * @param itemRemovido nó cujo conteúdo será substituído pelo do antecessor.
     * @param raizArvore raiz da sub-árvore onde se procura o antecessor.
     * @return a raiz da sub-árvore esquerda após a remoção do antecessor.
     */
    private No<K, V> substituir(No<K, V> itemRemovido, No<K, V> raizArvore) {

    	if (raizArvore.getDireita() != null) {
    		raizArvore.setDireita(substituir(itemRemovido, raizArvore.getDireita()));
    	} else {
    		itemRemovido.setChave(raizArvore.getChave());
    		itemRemovido.setItem(raizArvore.getItem());
    		raizArvore = raizArvore.getEsquerda();
    		tamanho--;
    	}

    	return raizArvore;
    }

    
    public Lista<V> recortar(K chaveDeOnde, K chaveAteOnde) {
		Lista<V> recorte = new Lista<>();
		recortar(raiz, chaveDeOnde, chaveAteOnde, recorte);
		return recorte;
	}

	/**
	 * Percorre recursivamente a árvore, em ordem crescente de chave, coletando na lista
	 * todos os itens cujas chaves pertençam ao intervalo [chaveDeOnde, chaveAteOnde].
	 *
	 * Explora a propriedade de ordenação da ABB para podar sub-árvores que certamente
	 * estão fora do intervalo: se a chave da raiz é menor do que o limite inferior, toda
	 * a sub-árvore esquerda é descartada; se é maior do que o limite superior, toda a
	 * sub-árvore direita é descartada.
	 *
	 * @param raizArvore raiz da (sub-)árvore visitada.
	 * @param chaveDeOnde limite inferior do intervalo (inclusivo).
	 * @param chaveAteOnde limite superior do intervalo (inclusivo).
	 * @param recorte lista que acumula os itens encontrados, em ordem crescente de chave.
	 */
	private void recortar(No<K, V> raizArvore, K chaveDeOnde, K chaveAteOnde, Lista<V> recorte) {

		if (raizArvore == null)
			return;

		int comparacaoInferior = comparador.compare(raizArvore.getChave(), chaveDeOnde);
		int comparacaoSuperior = comparador.compare(raizArvore.getChave(), chaveAteOnde);

		if (comparacaoInferior > 0)
			recortar(raizArvore.getEsquerda(), chaveDeOnde, chaveAteOnde, recorte);

		if (comparacaoInferior >= 0 && comparacaoSuperior <= 0)
			recorte.inserir(raizArvore.getItem());

		if (comparacaoSuperior < 0)
			recortar(raizArvore.getDireita(), chaveDeOnde, chaveAteOnde, recorte);
	}

	@Override
	public int tamanho() {
		return tamanho;
	}
	
	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000;
	}
}