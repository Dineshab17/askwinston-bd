drop procedure if exists schema_change;

delimiter ;;
create procedure schema_change() begin

drop table if exists user_answer;
drop table if exists answer;
drop table if exists question;
drop table if exists message;
drop table if exists doctor_busy_slot;
delete from notification_persistence_dto;
delete from notification_template_persistence_dto
    where notification_template_persistence_dto.name = 'Notify Patient About New Message' or notification_template_persistence_dto.name = 'Notify Info About Message From Patient';
if exists (select * from information_schema.columns where table_name = 'billing_card' and column_name = 'name') then
  alter table billing_card drop column `name`;
end if;
if exists (select * from information_schema.columns where table_name = 'cart_item' and column_name = 'subscription') then
  alter table cart_item drop column `subscription`;
end if;
if exists (select * from information_schema.columns where table_name = 'product' and column_name = 'cover_img') then
  alter table product drop column `cover_img`;
end if;
if exists (select * from information_schema.columns where table_name = 'product_subscription_item' and column_name = 'product_info') then
  alter table product_subscription_item drop column `product_info`;
end if;
if exists (select * from information_schema.columns where table_name = 'purchase_order_item' and column_name = 'product_info') then
  alter table purchase_order_item drop column `product_info`;
end if;
if exists (select * from information_schema.columns where table_name = 'purchase_order_item' and column_name = 'subscription') then
    alter table purchase_order_item drop column `subscription`;
end if;
if exists (select * from information_schema.columns where table_name = 'purchase_order' and column_name = 'appointment_date') then
  alter table purchase_order drop column `appointment_date`;
end if;
if exists (select * from information_schema.columns where table_name = 'purchase_order' and column_name = 'rejection_notes') then
  alter table purchase_order drop column `rejection_notes`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'doctors_address') then
  alter table `user` drop column `doctors_address`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'doctors_fax') then
  alter table `user` drop column `doctors_fax`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'doctor_signature_img') then
  alter table `user` drop column `doctor_signature_img`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'doctors_business_phone') then
  alter table `user` drop column `doctors_business_phone`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'doctors_credentials') then
  alter table `user` drop column `doctors_credentials`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'id_img') then
  alter table `user` drop column `id_img`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'id_text') then
  alter table `user` drop column `id_text`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'insurance_img') then
  alter table `user` drop column `insurance_img`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'is_questionnaire_filled') then
  alter table `user` drop column `is_questionnaire_filled`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'stripe_id') then
  alter table `user` drop column `stripe_id`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'id_text_type') then
  alter table `user` drop column `id_text_type`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'id_type') then
  alter table `user` drop column `id_type`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'ohip') then
  alter table `user` drop column `ohip`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'doctors_signature_img') then
  alter table `user` drop column `doctors_signature_img`;
end if;
if exists (select * from information_schema.columns where table_name = 'user' and column_name = 'doctors_billing_number') then
  alter table `user` drop column `doctors_billing_number`;
end if;

end;;

delimiter ;
call schema_change();

drop procedure if exists schema_change;


