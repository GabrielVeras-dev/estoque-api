package com.gabrielveras.estoque_api.ia;

import com.gabrielveras.estoque_api.dto.response.ProdutoResponse;
import com.gabrielveras.estoque_api.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueIaService {

    private final ChatClient.Builder chatClientBuilder;
    private final ProdutoService produtoService;

    private ChatClient chatClient() {
        return chatClientBuilder.build();
    }

    public String analisarEstoqueBaixo() {
        List<ProdutoResponse> produtos = produtoService.listarComEstoqueBaixo();

        if (produtos.isEmpty()) {
            return "Todos os produtos estão com estoque adequado. Nenhuma ação necessária.";
        }

        String listaProdutos = produtos.stream()
                .map(p -> String.format(
                        "- %s (Código: %s) | Estoque atual: %d | Estoque mínimo: %d",
                        p.getNome(),
                        p.getCodigo() != null ? p.getCodigo() : "N/A",
                        p.getQuantidadeEstoque(),
                        p.getEstoqueMinimo()
                ))
                .reduce("", (a, b) -> a + "\n" + b);

        String prompt = """
                Você é um especialista em gestão de estoque. Analise os produtos abaixo com estoque abaixo do mínimo
                e forneça um relatório executivo em português com:
                1. Resumo da situação atual
                2. Produtos em situação crítica (prioridade alta)
                3. Recomendações específicas de reposição para cada produto
                4. Sugestão de quantidade ideal para repor considerando uma margem de segurança de 30%%
                
                Produtos com estoque baixo:
                %s
                
                Seja objetivo e direto. Use linguagem profissional mas acessível para um gerente.
                """.formatted(listaProdutos);

        return chatClient().prompt(prompt).call().content();
    }

    public String analisarMercadoProduto(Long produtoId) {
        ProdutoResponse produto = produtoService.buscarPorId(produtoId);

        String prompt = """
                Você é um analista de mercado especializado em varejo e e-commerce brasileiro.
                Analise o seguinte produto e forneça um relatório completo em português:
                
                Produto: %s
                Categoria: %s
                Preço de custo: R$ %s
                Preço de venda: R$ %s
                Estoque atual: %d unidades
                
                Por favor forneça:
                1. TENDÊNCIA DE MERCADO: Este produto está em alta ou em baixa atualmente no Brasil?
                   Explique o motivo com dados e contexto atual.
                2. SCORE DE MERCADO: Dê uma nota de 1 a 10 para o potencial de venda deste produto agora.
                3. ANÁLISE DE PREÇO: O preço de venda está competitivo para o mercado atual?
                4. PRODUTOS COMPLEMENTARES EM ALTA: Liste 3 a 5 produtos da mesma categoria que estão
                   em tendência de alta no mercado e que poderiam ser adicionados ao estoque para
                   potencializar as vendas.
                5. RECOMENDAÇÃO FINAL: Devo manter, aumentar ou reduzir o estoque deste produto?
                
                Base sua análise em tendências reais do mercado brasileiro atual.
                """.formatted(
                produto.getNome(),
                produto.getCategoriaNome() != null ? produto.getCategoriaNome() : "Não categorizado",
                produto.getPrecoCusto(),
                produto.getPrecoVenda(),
                produto.getQuantidadeEstoque()
        );

        return chatClient().prompt(prompt).call().content();
    }

    public String sugerirProdutosEmAlta(String categoria) {
        String prompt = """
                Você é um consultor especializado em tendências de mercado para o varejo brasileiro.
                
                Para a categoria "%s", forneça uma análise detalhada em português com:
                
                1. TOP 5 PRODUTOS EM ALTA: Liste os 5 produtos mais procurados desta categoria
                   no mercado brasileiro atualmente, com uma breve explicação do motivo da alta.
                   
                2. TENDÊNCIA GERAL: Como está o mercado desta categoria no Brasil agora?
                
                3. PERFIL DO CONSUMIDOR: Quem está comprando estes produtos e por quê?
                
                4. MELHOR ÉPOCA PARA VENDER: Existe sazonalidade para estes produtos?
                   Se sim, quando é o pico de vendas?
                
                5. ESTRATÉGIA DE PREÇO: Qual a faixa de preço mais competitiva para estes produtos?
                
                6. AVISO DE RISCO: Existe algum produto desta categoria em queda que devo evitar?
                
                Seja específico com nomes de produtos reais e dados do mercado brasileiro atual.
                """.formatted(categoria);

        return chatClient().prompt(prompt).call().content();
    }

    public String gerarRelatorioExecutivo() {
        List<ProdutoResponse> todos = produtoService.listarTodos();
        List<ProdutoResponse> estoqueBaixo = produtoService.listarComEstoqueBaixo();

        long totalProdutos = todos.size();
        long produtosAtivos = todos.stream().filter(ProdutoResponse::getAtivo).count();
        long produtosCriticos = estoqueBaixo.size();

        String prompt = """
                Você é um diretor de operações analisando o relatório de estoque da empresa.
                Gere um relatório executivo completo em português com base nos dados abaixo:
                
                DADOS DO ESTOQUE:
                - Total de produtos cadastrados: %d
                - Produtos ativos: %d
                - Produtos com estoque crítico: %d
                
                PRODUTOS COM ESTOQUE CRÍTICO:
                %s
                
                O relatório deve conter:
                1. RESUMO EXECUTIVO (máximo 3 linhas)
                2. SITUAÇÃO DO ESTOQUE (saúde geral em %%  de produtos OK vs críticos)
                3. AÇÕES URGENTES (o que precisa ser feito hoje)
                4. AÇÕES DE CURTO PRAZO (próximos 7 dias)
                5. RECOMENDAÇÕES ESTRATÉGICAS (melhorias no processo de gestão)
                
                Escreva como um relatório profissional que será lido pelo CEO da empresa.
                """.formatted(
                totalProdutos,
                produtosAtivos,
                produtosCriticos,
                estoqueBaixo.isEmpty() ? "Nenhum produto em situação crítica." :
                        estoqueBaixo.stream()
                                .map(p -> String.format("- %s: %d/%d unidades",
                                        p.getNome(), p.getQuantidadeEstoque(), p.getEstoqueMinimo()))
                                .reduce("", (a, b) -> a + "\n" + b)
        );

        return chatClient().prompt(prompt).call().content();
    }
}