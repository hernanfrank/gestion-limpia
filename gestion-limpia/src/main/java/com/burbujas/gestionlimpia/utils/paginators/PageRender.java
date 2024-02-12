package com.burbujas.gestionlimpia.utils.paginators;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que genera paginación para cualquier clase
 * y posee algunas otras funciones para paginación
 */
public class PageRender<T> {
    private String url;
    private Page<T> page;

    private int totalPaginas;
    private int numElemPorPagina;
    private int paginaActual;

    private List<PageItem> paginas;

    // funcion q dibuja el paginador
    public PageRender(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.paginas = new ArrayList<PageItem>();

        // obtenemos la cantidad de elementos por página de page
        numElemPorPagina = page.getSize();

        // obtenemos el total de paginas de page
        totalPaginas = page.getTotalPages();

        // obtenemos pagina actual de page
        paginaActual = page.getNumber() + 1;

        int desde, hasta;
        // calculamos el rango de paginas a mostrar en el paginador
        if (totalPaginas <= numElemPorPagina) {
            // si entran en el total de paginas mostrables mostramos todo
            desde = 1;
            hasta = totalPaginas;
        } else {
            // sino se muestran por rango
            if (paginaActual <= numElemPorPagina / 2) {
                // rango inicio
                desde = 1;
                hasta = numElemPorPagina;
            } else if (paginaActual >= totalPaginas - numElemPorPagina / 2) {
                // rango fin
                desde = totalPaginas - numElemPorPagina + 1;
                hasta = numElemPorPagina;
            } else {
                // rango medio
                desde = paginaActual - numElemPorPagina / 2;
                hasta = numElemPorPagina;
            }
        }

        // llenamos la barra de paginación
        for (int i = 0; i < hasta; i++) {
            paginas.add(new PageItem(desde + i, paginaActual == desde + i));
        }
    }

    //getters
    public String getUrl() {
        return url;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public List<PageItem> getPaginas() {
        return paginas;
    }


    // metodos de la pagina actual
    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean isHasNext() {
        return page.hasNext();
    }

    public boolean isHasPrevious() {
        return page.hasPrevious();
    }
}
