package com.agent.agentApi.rest.seeder;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.agent.agentApi.rest.auth.UserRole;
import com.agent.agentApi.rest.entity.DishEntity;
import com.agent.agentApi.rest.entity.UserEntity;
import com.agent.agentApi.rest.repository.DishRepository;
import com.agent.agentApi.rest.repository.UserRepository;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, DishRepository dishRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedDishes();
    }

    private void seedAdmin() {
        String adminId = "admin-001";
        String adminEmail = "admin@smartmenu.com";

        if (!userRepository.existsById(adminId) && !userRepository.existsByEmail(adminEmail)) {
            UserEntity admin = new UserEntity(
                    adminId,
                    "Admin",
                    adminEmail,
                    passwordEncoder.encode("senha123"),
                    UserRole.ADMIN
            );
            userRepository.save(admin);
            System.out.println("[DataSeeder] Admin criado: " + adminEmail);
        }
    }

    private void seedDishes() {
        List<DishEntity> dishes = List.of(
                new DishEntity("feijoada", "Feijoada Completa",
                        "Feijoada completa com feijão preto, carnes variadas (linguiça, costela, paio, bacon),"
                        + " acompanhada de arroz branco, couve refogada, farofa, laranja fatiada e torresmo crocante.",
                        "Prato Principal", new BigDecimal("65.00"), null,
                        List.of("feijão preto", "linguiça", "costela de porco", "paio", "bacon", "alho", "cebola", "sal", "louro")),
                new DishEntity("coxinha", "Coxinha de Frango",
                        "Coxinha crocante de massa de batata recheada com frango desfiado temperado com requeijão cremoso.",
                        "Aperitivo", new BigDecimal("8.50"), null,
                        List.of("frango", "requeijão", "batata", "farinha de trigo", "caldo de galinha", "cebola", "alho", "sal")),
                new DishEntity("pao-de-queijo", "Pão de Queijo Mineiro",
                        "Pão de queijo mineiro tradicional feito com polvilho azedo e queijo meia-cura, crocante por fora e macio por dentro.",
                        "Aperitivo", new BigDecimal("6.00"), null,
                        List.of("polvilho azedo", "queijo meia-cura", "leite", "óleo", "ovos", "sal")),
                new DishEntity("moqueca", "Moqueca Baiana",
                        "Moqueca baiana de peixe com leite de coco, azeite de dendê, pimentões, tomates e coentro fresco,"
                        + " servida com arroz branco e farofa.",
                        "Prato Principal", new BigDecimal("58.00"), null,
                        List.of("peixe", "leite de coco", "azeite de dendê", "pimentão", "tomate", "cebola", "coentro", "alho", "limão", "sal")),
                new DishEntity("brigadeiro", "Brigadeiro Gourmet",
                        "Brigadeiro gourmet com chocolate belga e granulado fino, textura cremosa e sabor intenso de chocolate.",
                        "Sobremesa", new BigDecimal("5.00"), null,
                        List.of("chocolate em pó", "leite condensado", "manteiga", "granulado")),
                new DishEntity("acaraje", "Acarajé Baiano",
                        "Acarajé baiano tradicional feito com massa de feijão fradinho frito no azeite de dendê,"
                        + " recheado com vatapá, caruru, salada e pimenta.",
                        "Aperitivo", new BigDecimal("15.00"), null,
                        List.of("feijão fradinho", "azeite de dendê", "cebola", "sal", "vatapá", "caruru")),
                new DishEntity("churrasco", "Churrasco Gaúcho",
                        "Churrasco gaúcho com picanha, costela, linguiça e frango, servido com arroz, farofa, vinagrete"
                        + " e pão de alho.",
                        "Prato Principal", new BigDecimal("89.00"), null,
                        List.of("picanha", "costela", "linguiça", "frango", "sal grosso", "alho", "farofa", "vinagrete")),
                new DishEntity("acai", "Tigela de Açaí",
                        "Tigela de açaí cremoso com granola, banana fatiada, morango, leite em pó e mel.",
                        "Sobremesa", new BigDecimal("22.00"), null,
                        List.of("açaí", "granola", "banana", "morango", "leite em pó", "mel")),
                new DishEntity("vatapa", "Vatapá Baiano",
                        "Vatapá baiano cremoso feito com pão, leite de coco, azeite de dendê, camarão seco, amendoim e castanha.",
                        "Prato Principal", new BigDecimal("45.00"), null,
                        List.of("camarão seco", "leite de coco", "azeite de dendê", "pão", "amendoim", "castanha", "cebola", "alho", "tomate", "coentro", "sal")),
                new DishEntity("caipirinha", "Caipirinha Clássica",
                        "Caipirinha clássica brasileira feita com cachaça artesanal, limão tahiti fresco e açúcar.",
                        "Bebida", new BigDecimal("18.00"), null,
                        List.of("cachaça", "limão", "açúcar", "gelo"))
        );

        for (DishEntity dish : dishes) {
            if (!dishRepository.existsById(dish.getId())) {
                dishRepository.save(dish);
                System.out.println("[DataSeeder] Prato criado: " + dish.getTitle());
            }
        }
    }
}
