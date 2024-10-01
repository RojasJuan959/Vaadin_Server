package com.example;

import Controller.Procesos;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Route("")
public class Index extends Composite<VerticalLayout> {

    private static Grid<String[]> grid;
    private static Grid<String[]> carritoGrid;
    private static ArrayList<String[]> referenciaProductos;
    private static String[] selectedProduct = null;
    private static List<String[]> productosSeleccionados = new ArrayList<>();

    private Dialog imageDialog;
    private Image largeImage;

    private static TextField imageUrlField = new TextField("URL de Imagen");
    private static TextField nameField = new TextField("Nombre del Producto");
    private static TextField referenceField = new TextField("Referencia");
    private static MultiSelectComboBox<String> sizeComboBox = new MultiSelectComboBox<>("Tallas");
    private static TextField priceField = new TextField("Precio");

    private static Button createButton = new Button("Crear Producto", event -> {
        if (validateForm(imageUrlField, nameField, referenceField, sizeComboBox, priceField)) {
            if (selectedProduct == null) {
                // Crear nuevo producto
                agregarProductoAlGrid(imageUrlField.getValue(), nameField.getValue(), referenceField.getValue(),
                        String.join(",", sizeComboBox.getValue()), priceField.getValue());
                Notification.show("Producto creado exitosamente");
            } else {
                // Editar producto existente
                editarProductoEnGrid(imageUrlField.getValue(), nameField.getValue(), referenceField.getValue(),
                        String.join(",", sizeComboBox.getValue()), priceField.getValue());
                Notification.show("Producto actualizado exitosamente");
            }
            imageUrlField.clear();
            nameField.clear();
            referenceField.clear();
            sizeComboBox.clear();
            priceField.clear();
            selectedProduct = null;
        } else {
            Notification.show("Por favor, completa todos los campos.", 3000, Notification.Position.MIDDLE);
        }
    });

    public Index() throws Exception{

        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout leftSide = new VerticalLayout();
        VerticalLayout rightSide = new VerticalLayout();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");

        leftSide.getStyle().set("flex-grow", "1");

        rightSide.setWidth("100%");
        rightSide.getStyle().set("flex-grow", "1");

        getContent().add(layoutRow);

        layoutRow.add(leftSide);
        layoutRow.add(rightSide);

        crearHero(rightSide);
        crearFormularioAgregar(leftSide);
    }

    private void crearHero(VerticalLayout rightSide) throws Exception {

        VerticalLayout heroLayout = new VerticalLayout();
        H1 title = new H1("Walking -n- Clouds");
        Div actionCall = new Div();

        Html swiperHtml;
        Div sliderContainer;

        heroLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        heroLayout.setWidthFull();

        actionCall.setText("Tú eliges la calidad de tus pasos");

        swiperHtml = new Html("<swiper-container class='mySwiper' effect='cards' grab-cursor='true' style='width: 300px; height: 400px;'>" +
                "<swiper-slide><img src='https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/41be6b7655a24cd99908fc7a5e2e6946_9366/aSMC_ULTRABOOST_5_Blanco_IE8770_HM1.jpg' alt='Imagen 1' style='width:100%; height:100%; object-fit: cover;'/></swiper-slide>" +
                "<swiper-slide><img src='https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/dce93a1d-ebcb-4300-b8cf-f71da0820ea1/AIR+FORCE+1+%2707.png' alt='Imagen 2' style='width:100%; height:100%; object-fit: cover;'/></swiper-slide>" +
                "<swiper-slide><img src='https://trcmnbco.s3.amazonaws.com/BB550VGA_1.jpg' alt='Imagen 3' style='width:100%; height:100%; object-fit: cover;'/></swiper-slide>" +
                "</swiper-container>");

        sliderContainer = new Div(swiperHtml);

        sliderContainer.setWidth("300px");
        sliderContainer.setHeight("400px");
        sliderContainer.getStyle().set("margin-bottom", "20px");

        grid = new Grid<>();
        referenciaProductos = Index.invocarProductos();

        imageDialog = new Dialog();
        largeImage = new Image();
        largeImage.setWidth("500px");
        imageDialog.add(largeImage);

        grid.addComponentColumn(product -> {
            Image image = new Image(product[4], "Producto");
            image.setWidth("100%");
            image.setHeight("auto");  // Ajustar automáticamente la altura para mantener el aspecto
            image.getStyle().set("cursor", "pointer");

            image.addClickListener(event -> {
                largeImage.setSrc(product[4]);
                imageDialog.open();
            });

            return image;
        }).setHeader("Imagen");

        grid.addColumn(row -> row[0]).setHeader("Producto").setFlexGrow(0).setWidth("230px");
        grid.addColumn(row -> row[1]).setHeader("Referencia");
        grid.addColumn(row -> row[2]).setHeader("Talla");
        grid.addColumn(row -> row[3]).setHeader("Precio");

        grid.addComponentColumn(product -> {
            Checkbox checkbox = new Checkbox();
            checkbox.addValueChangeListener(event -> {
                if (event.getValue()) {
                    productosSeleccionados.add(product);
                } else {
                    productosSeleccionados.remove(product);
                }
                actualizarCarrito();
            });
            return checkbox;
        }).setHeader("Seleccionar");

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedProduct = event.getValue();
            if (selectedProduct != null) {
                cargarProductoAlFormulario(selectedProduct);
            }
            else {
                limpiarFormulario();
            }
        });

        grid.setItems(referenciaProductos);

        heroLayout.add(title, actionCall, sliderContainer, grid);
        heroLayout.setPadding(false);
        heroLayout.setSpacing(false);

        UI.getCurrent().getPage().executeJs(
                "document.head.insertAdjacentHTML('beforeend', `"
                        + "<script src='https://cdn.jsdelivr.net/npm/swiper@11/swiper-element-bundle.min.js'></script>`);"
        );

        rightSide.add(heroLayout);
    }

    private static void crearFormularioAgregar(VerticalLayout leftSide){

        FormLayout formLayout = new FormLayout();
        H2 title = new H2("Lista de Compra");

        title.getStyle().set("margin", "20px");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)  // 1 columna en pantallas pequeñas
        );

        sizeComboBox.setItems("6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10");

        Hr hr = new Hr();

        imageUrlField.setRequired(true);
        nameField.setRequired(true);
        referenceField.setRequired(true);
        sizeComboBox.setRequired(true);
        priceField.setRequired(true);

        carritoGrid = new Grid<>();
        carritoGrid.addComponentColumn(product -> {
            Image image = new Image(product[4], "Producto");
            image.setWidth("80px");
            image.setHeight("auto");
            return image;
        }).setHeader("Imagen");

        carritoGrid.addColumn(product -> product[0]).setHeader("Producto");

        carritoGrid.setItems(productosSeleccionados);

        Button comprarButton = new Button("Comprar", event -> {
            if (productosSeleccionados.isEmpty()) {
                Notification.show("No has seleccionado ningún producto para comprar.", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Has comprado " + productosSeleccionados.size() + " productos.", 3000, Notification.Position.MIDDLE);
                productosSeleccionados.clear();
                actualizarCarrito();
            }
        });

        formLayout.add(nameField, referenceField, imageUrlField, sizeComboBox, priceField, createButton, hr, title, carritoGrid, comprarButton);

        leftSide.add(formLayout);
    }

    private static void actualizarCarrito() {
        carritoGrid.setItems(productosSeleccionados);
    }

    private void cargarProductoAlFormulario(String[] product) {
        imageUrlField.setValue(product[4]);
        nameField.setValue(product[0]);
        referenceField.setValue(product[1]);
        sizeComboBox.setValue(new HashSet<>(Arrays.asList(product[2].split(","))));
        priceField.setValue(product[3]);

        createButton.setText("Editar Producto");
    }

    public static void editarProductoEnGrid(String url, String nombre, String referencia, String talla, String precio) {
        if (selectedProduct != null) {
            selectedProduct[0] = nombre;
            selectedProduct[1] = referencia;
            selectedProduct[2] = talla;
            selectedProduct[3] = precio;
            selectedProduct[4] = url;
            grid.getDataProvider().refreshAll();
        }
    }

    private void limpiarFormulario() {
        imageUrlField.clear();
        nameField.clear();
        referenceField.clear();
        sizeComboBox.clear();
        priceField.clear();
        selectedProduct = null;
        createButton.setText("Crear Producto");
    }

    private static boolean validateForm(TextField imageUrlField, TextField nameField, TextField referenceField,
                                        MultiSelectComboBox<String> sizeComboBox, TextField priceField) {
        return !imageUrlField.isEmpty() && !nameField.isEmpty() && !referenceField.isEmpty() &&
                !sizeComboBox.isEmpty() && !priceField.isEmpty();
    }

    public static void agregarProductoAlGrid(String url, String nombre, String referencia, String talla, String precio) {
        referenciaProductos.add(new String[]{nombre, referencia, talla, precio, url});
        grid.setItems(referenciaProductos);
    }

    private static ArrayList<String[]> invocarProductos() throws Exception {
        try {
            return Procesos.obtenerProductos();
        } catch (Exception e) {
            throw new Exception("Error al invocar los productos (" + e.getMessage() + ")");
        }
    }
}
