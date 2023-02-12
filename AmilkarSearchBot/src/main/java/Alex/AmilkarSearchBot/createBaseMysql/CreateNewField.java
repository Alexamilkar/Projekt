package Alex.AmilkarSearchBot.createBaseMysql;

import Alex.AmilkarSearchBot.model.Field;
import Alex.AmilkarSearchBot.model.FieldCrudRepository;

public class CreateNewField implements Runnable{
    private final FieldCrudRepository fieldCrudRepository;

    public CreateNewField(FieldCrudRepository fieldCrudRepository) {
        this.fieldCrudRepository = fieldCrudRepository;
    }

    @Override
    public void run() {
        Field field1 = new Field();

        field1.setName("title");
        field1.setSelector("title");
        field1.setWeight(1);

        fieldCrudRepository.save(field1);

        Field field2 = new Field();

        field2.setName("body");
        field2.setSelector("body");
        field2.setWeight(0.8F);

        fieldCrudRepository.save(field2);
    }
}
