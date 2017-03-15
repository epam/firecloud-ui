// draws admin stats dashboard

function adminStats(element, response, workflowName) {
    var container = element;
    var data = response;
    
    // google.charts.load("current", {"packages":["gauge","corechart"]});
    
    var usersElement = element.childNodes[0];
    var workflowsElement = element.childNodes[1];
    var boxElement = element.childNodes[3];
    var timingElement = element.childNodes[4];

    // Users chart
    var usersDataTable = google.visualization.arrayToDataTable([
      ['User Type', 'Count'],
      ['With Submissions',     data.statistics.usersWhoSubmittedDuringWindow.value],
      ['Without Submissions',  data.statistics.currentTotalUsers.value - data.statistics.usersWhoSubmittedDuringWindow.value]
    ]);

    var usersOptions = {
      title: 'Users',
      slices: {1: {offset: 0.3}},
      width: 500, height: 500
    };

    var usersChart = new google.visualization.PieChart(usersElement);
    usersChart.draw(usersDataTable, usersOptions);
    /*
    var usersChart = new google.visualization.Gauge(usersElement);

    var usersDataTable = google.visualization.arrayToDataTable([
      ['Label', 'Value'],
      ['Users', data.statistics.usersWhoSubmittedDuringWindow.value]
    ]);

    var usersOptions = {
      title: "Users: Submitted/Total",
      width: 400, height: 400,
      redFrom: 90, redTo: data.statistics.currentTotalUsers.value,
      yellowFrom:75, yellowTo: 90,
      minorTicks: 5,
      max: data.statistics.currentTotalUsers.value
    };

    usersChart.draw(usersDataTable, usersOptions);
    */
    
    // Workflow bar chart
    var wfDataTable = google.visualization.arrayToDataTable([
         ['Entity', 'Count', { role: 'style' }],
         ['Submissions', data.statistics.submissionsDuringWindow.value, 'red'],
         ['Workflows', data.statistics.workflowsDuringWindow.value, 'blue'],
      ]);
    
    var wfView = new google.visualization.DataView(wfDataTable);
    wfView.setColumns([0, 1,
                     { calc: "stringify",
                       sourceColumn: 1,
                       type: "string",
                       role: "annotation" },
                     2]);

    var wfOptions = {
      title: "Analyses",
      width: 600,
      height: 400,
      bar: {groupWidth: "95%"},
      legend: { position: "none" },
    };
    var wfChart = new google.visualization.BarChart(workflowsElement);
    wfChart.draw(wfView, wfOptions);
    
    // Workflow box plot
    var wfPerUser = data.statistics.workflowsPerUser;
    var wfPerSub = data.statistics.workflowsPerSubmission;
    var subPerUser = data.statistics.submissionsPerUser;
    
    var boxData = google.visualization.arrayToDataTable([
      ['workflowsPerUser', wfPerUser.min, wfPerUser.mean-(wfPerUser.stddev/2), wfPerUser.mean+(wfPerUser.stddev/2), wfPerUser.max],
      ['workflowsPerSubmission', wfPerSub.min, wfPerSub.mean-(wfPerSub.stddev/2), wfPerSub.mean+(wfPerSub.stddev/2), wfPerSub.max],
      ['submissionsPerUser', subPerUser.min, subPerUser.mean-(subPerUser.stddev/2), subPerUser.mean+(subPerUser.stddev/2), subPerUser.max]
      // Treat the first row as data.
    ], true);

    var boxOptions = {
      title: "Submissions per User",
      legend: 'none',
      bar: { groupWidth: '100%' }, // Remove space between bars.
      candlestick: {
        fallingColor: { strokeWidth: 0, fill: '#a52714' }, // red
        risingColor: { strokeWidth: 0, fill: '#0f9d58' }   // green
      }
    };

    var boxChart = new google.visualization.CandlestickChart(boxElement);
    boxChart.draw(boxData, boxOptions);
    
    // Workflow timing plot
    var subTime = data.statistics.submissionRunTime;
    var wfTime = data.statistics.workflowRunTime;
    
    var timeData = google.visualization.arrayToDataTable([
      ['submissionRunTime', subTime.min, subTime.mean-(subTime.stddev/2), subTime.mean+(subTime.stddev/2), subTime.max],
      ['workflowRunTime', wfTime.min, wfTime.mean-(wfTime.stddev/2), wfTime.mean+(wfTime.stddev/2), wfTime.max]
      // Treat the first row as data.
    ], true);

    var timeOptions = {
      title: "Submission Timing",
      legend: 'none',
      bar: { groupWidth: '100%' }, // Remove space between bars.
      candlestick: {
        fallingColor: { strokeWidth: 0, fill: '#a52714' }, // red
        risingColor: { strokeWidth: 0, fill: '#0f9d58' }   // green
      }
    };

    var timeChart = new google.visualization.CandlestickChart(timingElement);
    timeChart.draw(timeData, timeOptions);
    
};

// stored to preserve function calls through Closure
window['adminStats'] = adminStats;
