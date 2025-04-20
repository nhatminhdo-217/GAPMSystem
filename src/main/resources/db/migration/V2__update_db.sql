CREATE TABLE dye_batch
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    create_at         datetime              NULL,
    update_at         datetime              NULL,
    liters_min        DECIMAL(10, 2)        NOT NULL,
    liters            DECIMAL(10, 2)        NOT NULL,
    cone_batch_weight DECIMAL(10, 2)        NOT NULL,
    deadline          date                  NOT NULL,
    planned_start     date                  NOT NULL,
    start_at          datetime              NULL,
    complete_at       datetime              NULL,
    work_status       ENUM('FINISHED', 'IN_PROGRESS', 'NOT_STARTED')          NOT NULL,
    test_status       ENUM('TESTED', 'TESTING', 'NOT_STARTED')          NOT NULL,
    dye_photo         VARCHAR(255)          NULL,
    leader_start_id   BIGINT                NULL,
    leader_end_id     BIGINT                NULL,
    dye_stage_id      BIGINT                NOT NULL,
    is_pass           BIT(1)                NULL,
    CONSTRAINT pk_dyebatch PRIMARY KEY (id)
);

CREATE TABLE dye_risk_assessment
(
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    create_at                     datetime              NULL,
    update_at                     datetime              NULL,
    is_humidity                   BIT(1)                NULL,
    is_color_true                 BIT(1)                NULL,
    is_light_true                 BIT(1)                NULL,
    is_color_fading               BIT(1)                NULL,
    is_medication                 BIT(1)                NULL,
    is_medicine_safe              BIT(1)                NULL,
    is_industrial_cleaning_stains BIT(1)                NULL,
    is_pass                       BIT(1)                NULL,
    dye_batch_id                  BIGINT                NOT NULL,
    create_by                     BIGINT                NOT NULL,
    CONSTRAINT pk_dyeriskassessment PRIMARY KEY (id)
);

CREATE TABLE packaging_batch
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    create_at           datetime              NULL,
    update_at           datetime              NULL,
    planned_start       date                  NOT NULL,
    received_product_at datetime              NOT NULL,
    deadline            date                  NOT NULL,
    start_at            datetime              NULL,
    complete_at         datetime              NULL,
    work_status       ENUM('FINISHED', 'IN_PROGRESS', 'NOT_STARTED')          NOT NULL,
    test_status       ENUM('TESTED', 'TESTING', 'NOT_STARTED')          NOT NULL,
    leader_start_id     BIGINT                NULL,
    leader_end_id       BIGINT                NULL,
    packaging_photo     VARCHAR(255)          NULL,
    packaging_stage_id  BIGINT                NOT NULL,
    winding_batch_id    BIGINT                NOT NULL,
    is_pass             BIT(1)                NULL,
    CONSTRAINT pk_packaging_batch PRIMARY KEY (id)
);

CREATE TABLE packaging_risk_assessment
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    create_at          datetime              NULL,
    update_at          datetime              NULL,
    first_stamp        BIT(1)                NULL,
    core_stamp         BIT(1)                NULL,
    dozen_stamp        BIT(1)                NULL,
    kcs_stamp          BIT(1)                NULL,
    is_pass            BIT(1)                NULL,
    packaging_batch_id BIGINT                NOT NULL,
    create_by          BIGINT                NOT NULL,
    CONSTRAINT pk_packagingriskassessment PRIMARY KEY (id)
);

CREATE TABLE photo_stage
(
    id                           BIGINT AUTO_INCREMENT NOT NULL,
    create_at                    datetime              NULL,
    update_at                    datetime              NULL,
    photo                        VARCHAR(255)          NULL,
    dye_risk_assessment_id       BIGINT                NULL,
    winding_risk_assessment_id   BIGINT                NULL,
    packaging_risk_assessment_id BIGINT                NULL,
    CONSTRAINT pk_photostage PRIMARY KEY (id)
);

CREATE TABLE shift
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    create_at     datetime              NULL,
    update_at     datetime              NULL,
    shift_name    VARCHAR(255)          NULL,
    shift_start   time                  NULL,
    shift_end     time                  NULL,
    `description` VARCHAR(255)          NULL,
    CONSTRAINT pk_shift PRIMARY KEY (id)
);

CREATE TABLE user_shift
(
    user_id  BIGINT NOT NULL,
    shift_id BIGINT NOT NULL,
    CONSTRAINT pk_user_shift PRIMARY KEY (user_id, shift_id)
);

CREATE TABLE winding_batch
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    create_at        datetime              NULL,
    update_at        datetime              NULL,
    planned_start    date                  NOT NULL,
    deadline         date                  NOT NULL,
    received_cone_at datetime              NOT NULL,
    start_at         datetime              NULL,
    complete_at      datetime              NULL,
    work_status       ENUM('FINISHED', 'IN_PROGRESS', 'NOT_STARTED')          NOT NULL,
    test_status       ENUM('TESTED', 'TESTING', 'NOT_STARTED')          NOT NULL,
    leader_start_id  BIGINT                NULL,
    leader_end_id    BIGINT                NULL,
    winding_photo    VARCHAR(255)          NULL,
    winding_stage_id BIGINT                NOT NULL,
    is_pass          BIT(1)                NULL,
    dye_batch_id     BIGINT                NOT NULL,
    CONSTRAINT pk_winding_batch PRIMARY KEY (id)
);

CREATE TABLE winding_risk_assessment
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    create_at           datetime              NULL,
    update_at           datetime              NULL,
    true_cone           INT                   NULL,
    false_cone          INT                   NULL,
    is_color_uniformity BIT(1)                NULL,
    is_color_fading     BIT(1)                NULL,
    is_pass             BIT(1)                NULL,
    create_by           BIGINT                NOT NULL,
    winding_batch_id    BIGINT                NOT NULL,
    CONSTRAINT pk_windingriskassessment PRIMARY KEY (id)
);

ALTER TABLE dye_stage
    ADD cone_batch_weight DECIMAL(10, 2) NULL;

ALTER TABLE dye_stage
    ADD cone_total_weight DECIMAL(10, 2) NULL;

ALTER TABLE dye_stage
    ADD planned_start date NULL;

ALTER TABLE dye_stage
    MODIFY cone_batch_weight DECIMAL(10, 2) NOT NULL;

ALTER TABLE dye_stage
    MODIFY cone_total_weight DECIMAL(10, 2) NOT NULL;

ALTER TABLE dye_machine
    ADD is_active BIT(1) DEFAULT 1 NULL;

ALTER TABLE winding_machine
    ADD is_active BIT(1) DEFAULT 1 NULL;

ALTER TABLE work_order_detail
    ADD planned_end_at datetime NULL;

ALTER TABLE work_order_detail
    ADD planned_start_at datetime NULL;

ALTER TABLE dye_stage
    MODIFY planned_start date NOT NULL;

ALTER TABLE packaging_stage
    ADD planned_start date NULL;

ALTER TABLE packaging_stage
    MODIFY planned_start date NOT NULL;

ALTER TABLE winding_stage
    ADD planned_start date NULL;

ALTER TABLE winding_stage
    MODIFY planned_start date NOT NULL;

ALTER TABLE packaging_batch
    ADD CONSTRAINT uc_packaging_batch_winding_batch UNIQUE (winding_batch_id);

ALTER TABLE production_order
    ADD CONSTRAINT uc_production_order_purchase_order UNIQUE (purchase_order_id);

ALTER TABLE purchase_order
    ADD CONSTRAINT uc_purchase_order_contract UNIQUE (contract_id);

ALTER TABLE purchase_order
    ADD CONSTRAINT uc_purchase_order_quotation UNIQUE (quotation_id);

ALTER TABLE quotation
    ADD CONSTRAINT uc_quotation_rfq UNIQUE (rfq_id);

ALTER TABLE solution
    ADD CONSTRAINT uc_solution_rfq UNIQUE (rfq_id);

ALTER TABLE winding_batch
    ADD CONSTRAINT uc_winding_batch_dye_batch UNIQUE (dye_batch_id);

CREATE UNIQUE INDEX IX_pk_cate_brand_price ON cate_brand_price (cate_id, brand_id, is_color);

ALTER TABLE dye_batch
    ADD CONSTRAINT FK_DYEBATCH_ON_DYE_STAGE FOREIGN KEY (dye_stage_id) REFERENCES dye_stage (id);

ALTER TABLE dye_batch
    ADD CONSTRAINT FK_DYEBATCH_ON_LEADER_END FOREIGN KEY (leader_end_id) REFERENCES user (id);

ALTER TABLE dye_batch
    ADD CONSTRAINT FK_DYEBATCH_ON_LEADER_START FOREIGN KEY (leader_start_id) REFERENCES user (id);

ALTER TABLE dye_risk_assessment
    ADD CONSTRAINT FK_DYERISKASSESSMENT_ON_CREATE_BY FOREIGN KEY (create_by) REFERENCES user (id);

ALTER TABLE dye_risk_assessment
    ADD CONSTRAINT FK_DYERISKASSESSMENT_ON_DYE_BATCH FOREIGN KEY (dye_batch_id) REFERENCES dye_batch (id);

ALTER TABLE packaging_risk_assessment
    ADD CONSTRAINT FK_PACKAGINGRISKASSESSMENT_ON_CREATE_BY FOREIGN KEY (create_by) REFERENCES user (id);

ALTER TABLE packaging_risk_assessment
    ADD CONSTRAINT FK_PACKAGINGRISKASSESSMENT_ON_PACKAGING_BATCH FOREIGN KEY (packaging_batch_id) REFERENCES packaging_batch (id);

ALTER TABLE packaging_batch
    ADD CONSTRAINT FK_PACKAGING_BATCH_ON_LEADER_END FOREIGN KEY (leader_end_id) REFERENCES user (id);

ALTER TABLE packaging_batch
    ADD CONSTRAINT FK_PACKAGING_BATCH_ON_LEADER_START FOREIGN KEY (leader_start_id) REFERENCES user (id);

ALTER TABLE packaging_batch
    ADD CONSTRAINT FK_PACKAGING_BATCH_ON_PACKAGING_STAGE FOREIGN KEY (packaging_stage_id) REFERENCES packaging_stage (id);

ALTER TABLE packaging_batch
    ADD CONSTRAINT FK_PACKAGING_BATCH_ON_WINDING_BATCH FOREIGN KEY (winding_batch_id) REFERENCES winding_batch (id);

ALTER TABLE photo_stage
    ADD CONSTRAINT FK_PHOTOSTAGE_ON_DYE_RISK_ASSESSMENT FOREIGN KEY (dye_risk_assessment_id) REFERENCES dye_risk_assessment (id);

ALTER TABLE photo_stage
    ADD CONSTRAINT FK_PHOTOSTAGE_ON_PACKAGING_RISK_ASSESSMENT FOREIGN KEY (packaging_risk_assessment_id) REFERENCES packaging_risk_assessment (id);

ALTER TABLE photo_stage
    ADD CONSTRAINT FK_PHOTOSTAGE_ON_WINDING_RISK_ASSESSMENT FOREIGN KEY (winding_risk_assessment_id) REFERENCES winding_risk_assessment (id);

ALTER TABLE user_shift
    ADD CONSTRAINT FK_USER_SHIFT_ON_SHIFT FOREIGN KEY (shift_id) REFERENCES shift (id);

ALTER TABLE user_shift
    ADD CONSTRAINT FK_USER_SHIFT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE winding_risk_assessment
    ADD CONSTRAINT FK_WINDINGRISKASSESSMENT_ON_CREATE_BY FOREIGN KEY (create_by) REFERENCES user (id);

ALTER TABLE winding_risk_assessment
    ADD CONSTRAINT FK_WINDINGRISKASSESSMENT_ON_WINDING_BATCH FOREIGN KEY (winding_batch_id) REFERENCES winding_batch (id);

ALTER TABLE winding_batch
    ADD CONSTRAINT FK_WINDING_BATCH_ON_DYE_BATCH FOREIGN KEY (dye_batch_id) REFERENCES dye_batch (id);

ALTER TABLE winding_batch
    ADD CONSTRAINT FK_WINDING_BATCH_ON_LEADER_END FOREIGN KEY (leader_end_id) REFERENCES user (id);

ALTER TABLE winding_batch
    ADD CONSTRAINT FK_WINDING_BATCH_ON_LEADER_START FOREIGN KEY (leader_start_id) REFERENCES user (id);

ALTER TABLE winding_batch
    ADD CONSTRAINT FK_WINDING_BATCH_ON_WINDING_STAGE FOREIGN KEY (winding_stage_id) REFERENCES winding_stage (id);

DROP TABLE purchase_order_price;

ALTER TABLE dye_stage
    DROP COLUMN actual_output;

ALTER TABLE dye_stage
    DROP COLUMN checked_by;

ALTER TABLE dye_stage
    DROP COLUMN cone_weight;

ALTER TABLE dye_stage
    DROP COLUMN dye_photo;

ALTER TABLE dye_stage
    DROP COLUMN leader_id;

ALTER TABLE dye_stage
    DROP COLUMN test_status;

ALTER TABLE packaging_stage
    DROP COLUMN actual_output;

ALTER TABLE packaging_stage
    DROP COLUMN checked_by;

ALTER TABLE packaging_stage
    DROP COLUMN leader_id;

ALTER TABLE packaging_stage
    DROP COLUMN packaging_photo;

ALTER TABLE packaging_stage
    DROP COLUMN test_status;

ALTER TABLE winding_stage
    DROP COLUMN actual_output;

ALTER TABLE winding_stage
    DROP COLUMN checked_by;

ALTER TABLE winding_stage
    DROP COLUMN leader_id;

ALTER TABLE winding_stage
    DROP COLUMN test_status;

ALTER TABLE winding_stage
    DROP COLUMN winding_photo;

ALTER TABLE quotation_detail
    DROP COLUMN brand_id;

ALTER TABLE quotation_detail
    DROP COLUMN category_id;

ALTER TABLE quotation_detail
    DROP COLUMN price;

ALTER TABLE quotation_detail
    DROP COLUMN product_id;

ALTER TABLE production_order_detail
    DROP COLUMN `description`;

ALTER TABLE purchase_order_detail
    MODIFY total_price DECIMAL;

ALTER TABLE purchase_order_detail
    MODIFY unit_price DECIMAL;

ALTER TABLE quotation_detail
    MODIFY unit_price DECIMAL;