%
%  manual.cls
%
%  Created by Daniel D. Senften on 2008-12-14.
%  Copyright (c) 2008-2012 Diso. All rights reserved.
%
\NeedsTeXFormat{LaTeX2e}\relax
\ProvidesClass{manual}[2017/08/10 Manual Class]
\DeclareOption{a4paper}{\PassOptionsToClass{\CurrentOption}{article}}
\DeclareOption{11pt}{\PassOptionsToClass{\CurrentOption}{article}}
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{article}}
\ExecuteOptions{a4paper,11pt}
\ProcessOptions\relax
\LoadClass{article}

\RequirePackage[ngerman]{babel}
\selectlanguage{ngerman}

\RequirePackage{minted}
\RequirePackage{graphicx}
\RequirePackage{fancyhdr} % Headings
\RequirePackage[T1]{fontenc}
\RequirePackage{fontspec}
%\RequirePackage[utf8]{inputenc} % Use utf-8 encoding for foreign characters
\RequirePackage{listings} % Package for including code in the document

\RequirePackage{beamerarticle} % Make BEAMER commands available for other document class

\RequirePackage{xcolor}
\DefineNamedColor{named}{Pantone}{cmyk}{55,15,0,0}

\RequirePackage[top=3cm,bottom=2cm,left=2cm,right=1.5cm,headsep=10pt,a4paper]{geometry} % Page margins
    
\RequirePackage{boxedminipage} % Surround parts of graphics with box
\RequirePackage{nextpage}
\RequirePackage{times}
\RequirePackage{url} % Typesetting URLs, path names, and the like
\RequirePackage{verbatim}
\RequirePackage{lastpage} % Last page reference
\RequirePackage{subfig}
\RequirePackage[colorlinks = true,
                linkcolor = black,
                urlcolor  = blue,
                citecolor = blue,
                anchorcolor = blue]{hyperref}
\RequirePackage[answerdelayed,lastexercise]{exercise}

\RequirePackage{longtable} % Making multipage tabulars
\RequirePackage{supertabular}
\RequirePackage{relsize,fancyvrb}
\RequirePackage{multicol,multirow}


\RequirePackage{tikz}
\RequirePackage{tikz-uml}
\RequirePackage{amssymb}

\newcommand\Figure[2]{%
  \begin{figure}[ht]
	\centering		
    \mode<presentation>{\includegraphics[width=10cm]{Images/#1.png}}
    \mode<article>{\includegraphics[width=12cm]{Images/#1.png}}
    \mode<article>{\caption{#2}}
    \label{fig:#1}
  \end{figure}
}

\AtBeginDocument{%
  \DeclareGraphicsExtensions{.pdf,.jpg,.tif}
  \maketitle
  \newpage
  \tableofcontents
  \parindent0em\parskip1em
  \newpage}

\parindent0em

\endinput