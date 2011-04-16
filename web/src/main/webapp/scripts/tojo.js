dojo.require("dojox.grid.DataGrid");
  dojo.require("dojox.data.JsonRestStore");

  dojo.addOnLoad(function() {
    var store4 = new dojox.data.JsonRestStore({target: ${app.all}});

    var layout4 = [{
                field: '$oid',
                name: 'Title of Movie',
                width: '200px'
                },
                {
                field: 'exchange_id',
                name: 'Producer',
                width: 'auto'
                }];

            // create a new grid:
            var grid4 = new dojox.grid.DataGrid({
                query: {
                    Title: '*'
                },
                store: store4,
                clientSort: true,
                rowSelector: '20px',
                structure: layout4
            },
            document.createElement('div'));

            // append the new grid to the div "gridContainer4":
            dojo.byId("gridContainer4").appendChild(grid4.domNode);

            // Call startup, in order to render the grid:
            grid4.startup();
        });