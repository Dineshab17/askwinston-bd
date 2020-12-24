UPDATE purchase_order
SET shipping_date = date
WHERE shipping_date is null;