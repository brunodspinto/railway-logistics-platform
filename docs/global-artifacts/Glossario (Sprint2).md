| Name                | UC    | Theme                  | Description                                                                              |
|---------------------|-------|------------------------|------------------------------------------------------------------------------------------|
| Bogies              | BDDAD | Rolling Stock          | Conjunto de rodas e suspensão que suporta o veículo.                                     |
| Container           | BDDAD | Rolling Stock          | Unidade padronizada de transporte que contém mercadoria.                                 |
| ContainerType       | BDDAD | Rolling Stock          | Classificação do contentor por tamanho ou propósito.                                     |
| ElectrificationType | BDDAD | Infrastructure         | Tipo de eletrificação suportada pela linha.                                              |
| Endpoint            | BDDAD | Infrastructure         | Estação que serve como origem ou destino de linhas ou fretes.                            |
| FreightYard         | BDDAD | Infrastructure         | Pátio dedicado a operações de carga, descarga e manobras.                                |
| Gauge               | BDDAD | Infrastructure         | Distância entre carris usada para compatibilidade com material circulante.               |
| IntermodalTerminal  | BDDAD | Infrastructure         | Terminal que permite troca entre modos como camião e comboio.                            |
| Line                | BDDAD | Infrastructure         | Conjunto contínuo de segmentos que liga dois endpoints e define um percurso ferroviário. |
| LineSegment         | BDDAD | Infrastructure         | Trecho individual da linha com tipo, comprimento, eletrificação e limite de carga.       | 
| LineSegmentsType    | BDDAD | Infrastructure         | Classificação do segmento quanto a via simples ou dupla.                                 |
| LoadStatus          | BDDAD | Rolling Stock          | Indicação se o vagão está carregado ou vazio.                                            |
| Locomotive          | BDDAD | Rolling Stock          | Veículo que fornece tração ao comboio.                                                   |
| LocomotiveFuelType  | BDDAD | Rolling Stock          | Tipo de alimentação energética como Diesel ou Elétrica.                                  | 
| LocomotiveModel     | BDDAD | Rolling Stock          | Modelo que define capacidades técnicas da locomotiva.                                    |
| Operator            | BDDAD | Operators              | Entidade responsável por operar locomotivas e vagões.                                    |
| RailLineOwner       | BDDAD | Infrastructure         | Entidade que detém e gere a linha ferroviária.                                           |
| Siding              | BDDAD | Infrastructure         | Via lateral usada para cruzamento em via simples.                                        |
| Station             | BDDAD | Infrastructure         | Local ferroviário com operações de chegada, partida e manobras.                          |
| StationType         | BDDAD | Infrastructure         | Categoria funcional da estação, como terminal ou pátio de carga.                         |
| Terminal            | BDDAD | Infrastructure         | Instalação destinada a operações logísticas ferroviárias.                                |
| TrackType           | BDDAD | Infrastructure         | Classificação funcional da via como principal ou secundária.                             |
| Wagon               | BDDAD | Rolling Stock          | Veículo utilizado para transporte específico de mercadorias.                             |
| WagonModel          | BDDAD | Rolling Stock          | Modelo que define características estruturais do vagão.                                  |
| WagonsType          | BDDAD | Rolling Stock          | Categoria do vagão, como Tank, Hopper ou Boxcar.                                         |
| AVL Index           | ESINF | Spatial Index          | Árvore balanceada usada para consultas eficientes.                                       |
| Aisle               | ESINF | Warehouse              | Corredor que organiza várias bays dentro do armazém.                                     |
| AllocationRow       | ESINF | Picking                | Fragmento que indica de que box é retirada a quantidade alocada.                         |
| BST Index           | ESINF | Spatial Index          | Árvore ordenada usada como índice por atributo.                                          |
| Bay                 | ESINF | Warehouse              | Unidade de armazenamento dentro do armazém seguindo FEFO/FIFO.                           |
| BestFitDecreasing   | ESINF | Picking                | Heurística que otimiza ocupação após ordenação.                                          |
| Box                 | ESINF | Inventory              | Caixa armazenada contendo SKU, quantidade e opcionalmente validade.                      |
| BoxLocation         | ESINF | Inventory              | Localização completa da box incluindo aisle e bay.                                       |
| CirculationLine     | ESINF | Warehouse              | Via usada para circulação interna no terminal.                                           |
| FEFO                | ESINF | Inventory              | Método em que expira primeiro sai primeiro.                                              |
| FIFO                | ESINF | Inventory              | Método em que entra primeiro sai primeiro.                                               |
| FirstFit            | ESINF | Picking                | Heurística que coloca picks no primeiro trolley possível.                                |
| FirstFitDecreasing  | ESINF | Picking                | Heurística FF aplicada após ordenar por quantidade.                                      |
| HaversineDistance   | ESINF | Spatial Index          | Cálculo de distância geográfica entre dois pontos.                                       |
| InspectionAction    | ESINF | Returns                | Decisão após inspeção como reintegrar ou descartar.                                      |
| IntermodalTransfer  | ESINF | Operations             | Transferência de carga entre diferentes modos de transporte.                             |
| KD-Tree             | ESINF | Spatial Index          | Estrutura 2D usada para pesquisa rápida por latitude e longitude.                        |
| LatitudeIndex       | ESINF | Spatial Index          | Índice ordenado por latitude.                                                            |
| LoadingOperation    | ESINF | Operations             | Processo de carregamento de mercadoria para vagões.                                      |
| LongitudeIndex      | ESINF | Spatial Index          | Índice ordenado por longitude.                                                           |
| Order               | ESINF | Orders                 | Pedido de expedição composto por várias linhas de produtos.                              |
| OrderLine           | ESINF | Orders                 | Linha do pedido que indica SKU e quantidade necessária.                                  |
| ParkingLine         | ESINF | Warehouse              | Via onde vagões permanecem estacionados.                                                 |
| PickWave            | ESINF | Picking                | Conjunto agrupado de operações de picking.                                               |
| PickingBatch        | ESINF | Picking                | Grupo organizado de alocações num único processo.                                        |
| PickingHeuristic    | ESINF | Picking                | Conjunto de regras utilizadas na atribuição de picks.                                    |
| PickingPlan         | ESINF | Picking                | Plano completo de recolha resultante das alocações.                                      |
| Quarantine          | ESINF | Returns                | Área onde devoluções aguardam inspeção antes de decisão.                                 |
| Return              | ESINF | Returns                | Devolução recebida antes de inspeção e triagem.                                          |
| ReturnReason        | ESINF | Returns                | Motivo que levou à devolução da mercadoria.                                              |
| SKU                 | ESINF | Inventory              | Identificador único de produto no inventário.                                            |
| StationSpatialNode  | ESINF | Spatial Index          | Nó que representa uma estação num KD-tree.                                               |
| TimeZoneGroup       | ESINF | Spatial Index          | Agrupamento utilizado para consultas por fuso horário.                                   |
| TimeZoneIndex       | ESINF | Spatial Index          | Índice que agrupa estações por fuso horário.                                             |
| Trolley             | ESINF | Picking                | Carrinho utilizado para transportar picks até ao dock.                                   |
| UnloadingOperation  | ESINF | Operations             | Processo de descarga da mercadoria de vagões.                                            |
| Warehouse           | ESINF | Warehouse              | Armazém organizado em corredores e bays para armazenar mercadorias.                      |
| WarehouseDock       | ESINF | Warehouse              | Área onde mercadoria é recebida ou expedida.                                             |
| LightSign           | ARQCP | Station Infrastructure | Sinal que indica o estado da via como livre, atribuída ou ocupada.                       |
| OperationLog        | ARQCP | Operations             | Registo cronológico das ações do operador.                                               |
| OperatorCommand     | ARQCP | Operations             | Comando emitido pelo operador para atuar sobre vias e sinais.                            |
| SensorHumidity      | ARQCP | Sensors                | Sensor que monitoriza a humidade relativa.                                               |
| SensorTemperature   | ARQCP | Sensors                | Sensor que monitoriza a temperatura da estação.                                          |
| SynopticBoard       | ARQCP | Interface              | Quadro que apresenta o estado de vias, sinais e sensores.                                |
| Track               | ARQCP | Station Infrastructure | Via interna da estação usada para circulação e estacionamento.                           |
| CombinedPower       | LAPR3 | Planning               | Potência total disponível das locomotivas do comboio.                                    |
| CrossingOperation   | LAPR3 | Planning               | Operação para cruzamento seguro em via simples.                                          |
| CrossingPoint       | LAPR3 | Planning               | Ponto específico onde pode ocorrer cruzamento.                                           |
| Dispatching         | LAPR3 | Operations             | Gestão em tempo real dos movimentos dos comboios.                                        |
| Freight             | LAPR3 | Train Domain           | Conjunto de vagões carregados destinados a transporte entre endpoints.                   |
| LocomotiveSet       | LAPR3 | Train Domain           | Conjunto de locomotivas atribuídas a um comboio.                                         |
| Path                | LAPR3 | Train Domain           | Sequência ordenada de estações efetivamente percorridas.                                 |
| Route               | LAPR3 | Train Domain           | Percurso planeado que pode envolver vários fretes e operações.                           |
| RouteSection        | LAPR3 | Planning               | Parte da rota entre duas estações consecutivas.                                          |
| Schedule            | LAPR3 | Planning               | Plano temporal com horas estimadas de passagem e paragem.                                |
| Scheduler           | LAPR3 | Planning               | Componente que calcula horários, cruzamentos e velocidades.                              |
| Scheduling          | LAPR3 | Operations             | Planeamento ordenado de operações ferroviárias.                                          |
| SectionSpeedLimit   | LAPR3 | Planning               | Velocidade máxima permitida num segmento.                                                |
| TotalWeight         | LAPR3 | Planning               | Peso total incluindo locomotivas, vagões e carga.                                        |
| Train               | LAPR3 | Train Domain           | Comboio composto por locomotivas e vagões configurado para uma rota.                     |
| TrainDispatch       | LAPR3 | Planning               | Processo de envio do comboio conforme planeamento.                                       |
| TrainSection        | LAPR3 | Train Domain           | Secção individual da rota entre dois pontos.                                             |
| TrainWeightLimit    | LAPR3 | Planning               | Limite máximo de peso permitido para o comboio.                                          |
| WagonSet            | LAPR3 | Train Domain           | Conjunto de vagões atribuídos ao comboio.                                                |
| operationSpeed      | LAPR3 | Planning               | Velocidade usada para calcular o tempo de viagem.                                        |