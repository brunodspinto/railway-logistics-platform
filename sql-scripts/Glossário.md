| Name                           | Labels      | Description                                                                        |
|--------------------------------|-------------|------------------------------------------------------------------------------------|
| LineSegmentsType               | Entity      | Categoria que define o tipo de segmento de uma linha ferroviária.                  |
| id_LineSegmentsType            | Attribute   | Identificador único da entidade LineSegmentsType, utilizado como chave primária.   |
| description_LineSegmentsType   | Attribute   | Descrição textual da entidade LineSegmentsType.                                    |
| LineSegment                    | Entity      | Trecho de linha ferroviária entre dois pontos.                                     |
| id_LineSegment                 | Attribute   | Identificador único da entidade LineSegment, utilizado como chave primária.        |
| maximumWeight                  | Attribute   | Peso máximo suportado pelo segmento de linha, em quilogramas por metro (kg/m).     |
| length_LineSegment             | Attribute   | Comprimento total, em metros.                                                      |
| isElectrified                  | Attribute   | Indicador lógico que assinala se o segmento de linha está eletrificado.            |
| segmentNoOrder                 | Attribute   | Número que define a ordem do segmento dentro da linha.                             |
| lineSegmentsTypeId             | Attribute   | Chave estrangeira que referencia a entidade lineSegmentsType.                      |
| lineId                         | Attribute   | Chave estrangeira que referencia a entidade line.                                  |
| Line                           | Entity      | Linha ferroviária composta por vários segmentos.                                   |
| id_Line                        | Attribute   | Identificador único da entidade Line, utilizado como chave primária.               |
| nameLine                       | Attribute   | Nome oficial da linha ferroviária.                                                 |
| ownerId                        | Attribute   | Chave estrangeira que referencia a entidade owner.                                 |
| startStation                   | Attribute   | Estação inicial.                                                                   |
| endStation                     | Attribute   | Estação final.                                                                     |
| gaugeId_Line                   | Attribute   | Chave estrangeira que referencia a entidade gauge.                                 |
| Station                        | Entity      | Local ferroviário onde ocorrem paragens ou operações de carga e descarga.          |
| idStation                      | Attribute   | Identificador da estação.                                                          |
| nameStation                    | Attribute   | Nome da estação.                                                                   |
| stationTypeId                  | Attribute   | Chave estrangeira que referencia a entidade stationType.                           |
| StationType                    | Entity      | Tipo de estação.                                                                   |
| id_StationType                 | Attribute   | Identificador único da entidade StationType, utilizado como chave primária.        |
| description_StationType        | Attribute   | Descrição textual da entidade StationType.                                         |
| Operator                       | Entity      | Empresa responsável por gerir e operar comboios ou linhas ferroviárias.            |
| vatNumber                      | Attribute   | Número fiscal da empresa.                                                          |
| name_Operator                  | Attribute   | Nome da empresa.                                                                   |
| shortName                      | Attribute   | Nome abreviado.                                                                    |
| Locomotive                     | Entity      | Veículo motorizado que fornece tração aos comboios de carga.                       |
| numberLocomotive               | Attribute   | Veículo motorizado que fornece tração aos comboios de carga.                       |
| yearOfEntry_Locomotive         | Attribute   | Ano de entrada em serviço.                                                         |
| operationSpeed                 | Attribute   | Velocidade operacional.                                                            |
| model                          | Attribute   | Referência ao modelo.                                                              |
| operator_Locomotive            | Attribute   | Empresa responsável por gerir e operar comboios ou linhas ferroviárias.            |
| LocomotiveModel                | Entity      | Veículo motorizado que fornece tração aos comboios de carga.                       |
| id_LocomotiveModel             | Attribute   | Identificador único da entidade LocomotiveModel, utilizado como chave primária.    |
| make                           | Attribute   | Fabricante.                                                                        |
| modelName                      | Attribute   | Nome do modelo.                                                                    |
| power                          | Attribute   | Potência.                                                                          |
| maxSpeed_LocomotiveModel       | Attribute   | Velocidade máxima.                                                                 |
| weight_LocomotiveModel         | Attribute   | Peso.                                                                              |
| length_LocomotiveModel         | Attribute   | Comprimento total, em metros.                                                      |
| width_LocomotiveModel          | Attribute   | Largura.                                                                           |
| height_LocomotiveModel         | Attribute   | Altura.                                                                            |
| traction                       | Attribute   | Tipo de tração.                                                                    |
| locomotiveFuelTypeId           | Attribute   | Chave estrangeira que referencia a entidade locomotiveFuelType.                    |
| bogieId                        | Attribute   | Chave estrangeira que referencia a entidade bogie.                                 |
| gaugeId_LocomotiveModel        | Attribute   | Chave estrangeira que referencia a entidade gauge.                                 |
| LocomotiveFuelType             | Entity      | Veículo motorizado que fornece tração aos comboios de carga.                       |
| id_LocomotiveFuelType          | Attribute   | Identificador único da entidade LocomotiveFuelType, utilizado como chave primária. |
| description_LocomotiveFuelType | Attribute   | Descrição textual da entidade LocomotiveFuelType.                                  |
| Electric                       | Entity      | Tipo elétrico de combustível.                                                      |
| id_Electric                    | Attribute   | Identificador único da entidade Electric, utilizado como chave primária.           |
| voltage                        | Attribute   | Voltagem.                                                                          |
| frequency                      | Attribute   | Frequência.                                                                        |
| locomotiveFuelType_Electric    | Attribute   | Veículo motorizado que fornece tração aos comboios de carga.                       |
| Diesel                         | Entity      | Tipo diesel de combustível.                                                        |
| id_Diesel                      | Attribute   | Identificador único da entidade Diesel, utilizado como chave primária.             |
| combustibleCapacity            | Attribute   | Capacidade de combustível.                                                         |
| locomotiveFuelType_Diesel      | Attribute   | Veículo motorizado que fornece tração aos comboios de carga.                       |
| Wagon                          | Entity      | Veículo de carga utilizado para transportar mercadorias na ferrovia.               |
| numberWagon                    | Attribute   | Veículo de carga utilizado para transportar mercadorias na ferrovia.               |
| weight_Wagon                   | Attribute   | Peso do vagão.                                                                     |
| yearOfEntry_Wagon              | Attribute   | Ano de entrada em serviço.                                                         |
| wagonModelId                   | Attribute   | Chave estrangeira que referencia a entidade wagonModel.                            |
| operator_Wagon                 | Attribute   | Empresa responsável por gerir e operar comboios ou linhas ferroviárias.            |
| WagonModel                     | Entity      | Veículo de carga utilizado para transportar mercadorias na ferrovia.               |
| id_WagonModel                  | Attribute   | Identificador único da entidade WagonModel, utilizado como chave primária.         |
| nameModel                      | Attribute   | Nome do modelo.                                                                    |
| length_WagonModel              | Attribute   | Comprimento total, em metros.                                                      |
| width_WagonModel               | Attribute   | Largura.                                                                           |
| height_WagonModel              | Attribute   | Altura.                                                                            |
| maxSpeed_WagonModel            | Attribute   | Velocidade máxima.                                                                 |
| maxLoad                        | Attribute   | Carga máxima.                                                                      |
| wagonsTypeId                   | Attribute   | Chave estrangeira que referencia a entidade wagonsType.                            |
| gaugeId_WagonModel             | Attribute   | Chave estrangeira que referencia a entidade gauge.                                 |
| WagonsType                     | Entity      | Veículo de carga utilizado para transportar mercadorias na ferrovia.               |
| id_WagonsType                  | Attribute   | Identificador único da entidade WagonsType, utilizado como chave primária.         |
| description_WagonsType         | Attribute   | Descrição textual da entidade WagonsType.                                          |
| Gauge                          | Entity      | Bitola.                                                                            |
| idGauge                        | Attribute   | Identificador da bitola.                                                           |
| name_Gauge                     | Attribute   | Nome da bitola.                                                                    |
| measure                        | Attribute   | Medida da bitola.                                                                  |
| Bogies                         | Entity      | Bogie.                                                                             |
| id_Bogies                      | Attribute   | Identificador único da entidade Bogies, utilizado como chave primária.             |
| nameBogie                      | Attribute   | Nome do bogie.                                                                     |