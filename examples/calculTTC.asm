DATA SEGMENT
	prixHt DD
	prixTtc DD
DATA ENDS
CODE SEGMENT
	in eax
	mov prixHt, eax
	push eax
	mov ebx, 119
	mov eax, prixHt
	mul eax, ebx
	push eax
	pop eax
	mov prixTtc, eax
	push eax
	mov ebx, 100
	mov eax, prixTtc
	div eax, ebx
	push eax
	pop eax
	mov prixTtc, eax
	push eax
	mov eax, prixTtc
	out eax
CODE ENDS
