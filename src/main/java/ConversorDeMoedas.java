import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConversorDeMoedas {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao Conversor de Moedas!");

        System.out.print("Digite a moeda base (exemplo: USD, EUR): ");
        String moedaBase = scanner.nextLine().toUpperCase();

        try {
            Map<String, Double> taxas = obterTaxasDeCambio(moedaBase);

            if (taxas == null) {
                System.out.println("Erro ao obter taxas de câmbio. Verifique a moeda base e tente novamente.");
                return;
            }

            boolean continuar = true;
            while (continuar) {
                System.out.println("\nEscolha uma conversão:");
                System.out.println("1. USD -> BRL");
                System.out.println("2. EUR -> BRL");
                System.out.println("3. BRL -> USD");
                System.out.println("4. BRL -> EUR");
                System.out.println("5. USD -> EUR");
                System.out.println("6. EUR -> USD");
                System.out.println("7. USD -> GBP");
                System.out.println("8. GBP -> USD");
                System.out.println("9. BRL -> GBP");
                System.out.println("10. GBP -> BRL");
                System.out.println("11. Sair");
                System.out.print("Opção: ");

                int opcao = scanner.nextInt();
                scanner.nextLine(); // Consumir a quebra de linha

                if (opcao == 11) {
                    System.out.println("Saindo... Obrigado por usar o Conversor de Moedas!");
                    continuar = false;
                    continue;
                }

                System.out.print("Digite o valor a ser convertido: ");
                double valor = scanner.nextDouble();

                String moedaDestino = "";
                double taxaConversao = 0;

                switch (opcao) {
                    case 1:
                        moedaDestino = "BRL";
                        taxaConversao = taxas.getOrDefault("BRL", 0.0);
                        break;
                    case 2:
                        moedaDestino = "BRL";
                        taxaConversao = taxas.getOrDefault("BRL", 0.0) / taxas.getOrDefault("EUR", 1.0);
                        break;
                    case 3:
                        moedaDestino = "USD";
                        taxaConversao = 1 / taxas.getOrDefault("BRL", 1.0);
                        break;
                    case 4:
                        moedaDestino = "EUR";
                        taxaConversao = taxas.getOrDefault("EUR", 0.0) / taxas.getOrDefault("BRL", 1.0);
                        break;
                    case 5:
                        moedaDestino = "EUR";
                        taxaConversao = taxas.getOrDefault("EUR", 0.0);
                        break;
                    case 6:
                        moedaDestino = "USD";
                        taxaConversao = 1 / taxas.getOrDefault("EUR", 1.0);
                        break;
                    case 7:
                        moedaDestino = "GBP";
                        taxaConversao = taxas.getOrDefault("GBP", 0.0);
                        break;
                    case 8:
                        moedaDestino = "USD";
                        taxaConversao = 1 / taxas.getOrDefault("GBP", 1.0);
                        break;
                    case 9:
                        moedaDestino = "GBP";
                        taxaConversao = taxas.getOrDefault("GBP", 0.0) / taxas.getOrDefault("BRL", 1.0);
                        break;
                    case 10:
                        moedaDestino = "BRL";
                        taxaConversao = taxas.getOrDefault("BRL", 0.0) / taxas.getOrDefault("GBP", 1.0);
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                }

                double resultado = valor * taxaConversao;
                System.out.printf("%.2f %s é igual a %.2f %s\n", valor, moedaBase, resultado, moedaDestino);
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static Map<String, Double> obterTaxasDeCambio(String moedaBase) throws Exception {
        String apiUrl = "https://api.exchangerate-api.com/v4/latest/" + moedaBase;
        HttpURLConnection conexao = (HttpURLConnection) new URL(apiUrl).openConnection();
        conexao.setRequestMethod("GET");

        int codigoResposta = conexao.getResponseCode();
        if (codigoResposta != 200) {
            System.out.println("Erro: não foi possível acessar a API. Código de resposta: " + codigoResposta);
            return null;
        }

        BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        StringBuilder resposta = new StringBuilder();
        String linha;
        while ((linha = leitor.readLine()) != null) {
            resposta.append(linha);
        }
        leitor.close();

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(resposta.toString(), JsonObject.class);
        JsonObject taxas = json.getAsJsonObject("rates");


        Map<String, Double> taxasMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : taxas.entrySet()) {
            taxasMap.put(entry.getKey(), taxas.get(entry.getKey()).getAsDouble());
        }

        return taxasMap;
    }
} 
