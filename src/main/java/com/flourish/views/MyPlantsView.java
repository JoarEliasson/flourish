package com.flourish.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.flourish.views.components.AvatarItem;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import java.util.List;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("My Plants")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class MyPlantsView extends Composite<VerticalLayout> {

    public MyPlantsView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        Button buttonPrimary = new Button();
        Button buttonPrimary2 = new Button();
        Button buttonPrimary3 = new Button();
        Button buttonPrimary4 = new Button();
        H1 h1 = new H1();
        H2 h2 = new H2();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        TextField textField = new TextField();
        MultiSelectListBox avatarItems = new MultiSelectListBox();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutRow2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        buttonPrimary.setText("My Plants");
        buttonPrimary.setWidth("200px");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("All Plants");
        buttonPrimary2.setWidth("150px");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary3.setText("Settings");
        buttonPrimary3.setWidth("150px");
        buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary4.setText("Notifications");
        buttonPrimary4.setWidth("150px");
        buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        h1.setText("Flowrish");
        h1.setWidth("max-content");
        h2.setText("My Plants");
        h2.setWidth("max-content");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        textField.setLabel("Text field");
        textField.setWidth("min-content");
        avatarItems.setWidth("min-content");
        setAvatarItemsSampleData(avatarItems);
        getContent().add(layoutRow);
        layoutRow.add(layoutRow2);
        layoutRow2.add(buttonPrimary);
        layoutRow2.add(buttonPrimary2);
        layoutRow2.add(buttonPrimary3);
        layoutRow2.add(buttonPrimary4);
        layoutRow2.add(h1);
        getContent().add(h2);
        getContent().add(layoutColumn2);
        layoutColumn2.add(textField);
        layoutColumn2.add(avatarItems);
    }

    private void setAvatarItemsSampleData(MultiSelectListBox multiSelectListBox) {
        record Person(String name, String profession) {
        }
        ;
        List<Person> data = List.of(new Person("Aria Bailey", "Endocrinologist"), new Person("Aaliyah Butler", "Nephrologist"), new Person("Eleanor Price", "Ophthalmologist"), new Person("Allison Torres", "Allergist"), new Person("Madeline Lewis", "Gastroenterologist"));
        multiSelectListBox.setItems(data);
        multiSelectListBox.setRenderer(new ComponentRenderer(item -> {
            AvatarItem avatarItem = new AvatarItem();
            avatarItem.setHeading(((Person) item).name);
            avatarItem.setDescription(((Person) item).profession);
            avatarItem.setAvatar(new Avatar(((Person) item).name));
            return avatarItem;
        }));
    }
}
